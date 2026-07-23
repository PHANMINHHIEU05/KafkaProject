package com.example.service;

import com.example.dto.CreatePostMediaRequest;
import com.example.dto.CreatePostRequest;
import com.example.dto.PostResponse;
import com.example.dto.PostSummaryResponse;
import com.example.entity.OutBox;
import com.example.entity.Post;
import com.example.entity.PostMedia;
import com.example.entity.PostTarget;
import com.example.entity.SocialAccount;
import com.example.entity.User;
import com.example.entity.enums.OutboxStatus;
import com.example.entity.enums.PostStatus;
import com.example.entity.enums.PublishStatus;
import com.example.event.PublishRequestedEvent;
import com.example.exception.BadRequestException;
import com.example.exception.ConflictException;
import com.example.exception.ErrorCode;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.OutboxMapper;
import com.example.mapper.PostMapper;
import com.example.mapper.PostMediaMapper;
import com.example.mapper.PostTargetMapper;
import com.example.mapper.PublishEventMapper;
import com.example.repository.PostRepository;
import com.example.repository.SocialAccountRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final String PUBLISH_REQUEST_TOPIC =
        "post-publish-requests";

    private static final String PUBLISH_REQUEST_EVENT_TYPE =
        "POST_PUBLISH_REQUESTED";

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final PostRepository postRepository;
    private final OutboxService outboxService;

    private final PostMapper postMapper;
    private final PostMediaMapper postMediaMapper;
    private final PostTargetMapper postTargetMapper;
    private final PublishEventMapper publishEventMapper;
    private final OutboxMapper outboxMapper;
    private final ObjectMapper objectMapper;

    /**
     * Tạo bài đăng và Outbox event trong cùng một transaction.
     */
    @Transactional
    public PostResponse createPost(
        Integer userId,
        CreatePostRequest request
    ) {
        User user = getUserOrThrow(userId);

        validateClientRequestId(
            userId,
            request.clientRequestId()
        );

        List<Integer> accountIds =
            normalizeAccountIds(request.socialAccountIds());

        List<SocialAccount> socialAccounts =
            getValidSocialAccounts(userId, accountIds);

        Post post = buildPost(
            user,
            request,
            socialAccounts
        );

        /*
         * Post cần cascade PERSIST tới PostMedia và PostTarget.
         */
        Post savedPost = postRepository.save(post);

        /*
         * Đẩy INSERT xuống database để phát hiện sớm lỗi
         * constraint và bảo đảm các entity con đã có ID.
         */
        postRepository.flush();

        OutBox outboxEvent =
            buildPublishRequestedEvent(savedPost);

        outboxService.save(outboxEvent);

        return postMapper.toResponse(savedPost);
    }

    /**
     * Lấy chi tiết bài đăng thuộc user.
     */
    @Transactional(readOnly = true)
    public PostResponse getPostById(
        Integer userId,
        Long postId
    ) {
        Post post = postRepository
            .findByIdAndUserId(postId, userId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    ErrorCode.POST_NOT_FOUND,
                    "Không tìm thấy bài đăng có id: " + postId
                )
            );

        return postMapper.toResponse(post);
    }

    /**
     * Lấy danh sách bài đăng.
     *
     * status == null: lấy tất cả bài của user.
     * status != null: lọc theo trạng thái.
     */
    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getPosts(
        Integer userId,
        PostStatus status,
        Pageable pageable
    ) {
        getUserOrThrow(userId);

        Page<Post> posts;

        if (status == null) {
            posts = postRepository.findAllByUserId(
                userId,
                pageable
            );
        } else {
            posts = postRepository.findAllByUserIdAndStatus(
                userId,
                status,
                pageable
            );
        }

        return posts.map(postMapper::toSummaryResponse);
    }

    @Transactional
    public PostResponse cancelPost(
        Integer userId,
        Long postId
    ) {
        Post post = postRepository
            .findByIdAndUserId(postId, userId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    ErrorCode.POST_NOT_FOUND,
                    "Không tìm thấy bài đăng có id: " + postId
                )
            );

        validatePostCanBeCancelled(post);

        boolean hasProcessingTarget = post.getTargets()
            .stream()
            .anyMatch(target ->
                target.getStatus() == PublishStatus.PROCESSING
            );

        if (hasProcessingTarget) {
            throw new ConflictException(
                ErrorCode.INVALID_POST_STATUS,
                "Không thể hủy vì bài đăng đang được xử lý"
            );
        }

        for (PostTarget target : post.getTargets()) {
            if (target.getStatus() == PublishStatus.PENDING) {
                target.setStatus(PublishStatus.CANCELLED);
            }
        }

        post.setStatus(PostStatus.CANCELLED);

        /*
         * Post đang là managed entity nên Hibernate tự update
         * khi transaction commit.
         */
        return postMapper.toResponse(post);
    }

    /**
     * Tạo Post cùng PostMedia và PostTarget.
     */
    private Post buildPost(
        User user,
        CreatePostRequest request,
        List<SocialAccount> socialAccounts
    ) {
        Post post = postMapper.toEntity(request);

        post.setUser(user);

        if (request.scheduledAt() == null) {
            post.setStatus(PostStatus.QUEUED);
        } else {
            validateScheduledAt(request.scheduledAt());
            post.setStatus(PostStatus.SCHEDULED);
        }

        addMedia(post, request.mediaList());
        addTargets(post, socialAccounts);

        return post;
    }

    /**
     * Chuyển media request thành PostMedia.
     */
    private void addMedia(
        Post post,
        List<CreatePostMediaRequest> mediaRequests
    ) {
        if (mediaRequests == null || mediaRequests.isEmpty()) {
            return;
        }

        for (CreatePostMediaRequest mediaRequest : mediaRequests) {
            PostMedia media =
                postMediaMapper.toEntity(mediaRequest);

            /*
             * addMedia phải đồng thời thực hiện:
             * post.getMedia().add(media);
             * media.setPost(post);
             */
            post.addMedia(media);
        }
    }

    /**
     * Mỗi SocialAccount tạo một PostTarget.
     */
    private void addTargets(
        Post post,
        List<SocialAccount> socialAccounts
    ) {
        for (SocialAccount account : socialAccounts) {
            PostTarget target = postTargetMapper.toEntity(account);

            /*
             * addTarget phải đồng thời thực hiện:
             * post.getTargets().add(target);
             * target.setPost(post);
             */
            post.addTarget(target);
        }
    }

    private User getUserOrThrow(Integer userId) {
        return userRepository.findById(userId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    ErrorCode.USER_NOT_FOUND,
                    "Không tìm thấy người dùng có id: " + userId
                )
            );
    }

    /**
     * Chống việc client gửi lại cùng một request tạo bài.
     */
    private void validateClientRequestId(
        Integer userId,
        String clientRequestId
    ) {
        if (clientRequestId == null
            || clientRequestId.isBlank()) {
            return;
        }

        boolean existed =
            postRepository.existsByUserIdAndClientRequestId(
                userId,
                clientRequestId
            );

        if (existed) {
            throw new ConflictException(
                ErrorCode.DUPLICATE_CLIENT_REQUEST,
                "clientRequestId đã tồn tại: "
                    + clientRequestId
            );
        }
    }

    /**
     * Kiểm tra và loại bỏ socialAccountId bị trùng.
     */
    private List<Integer> normalizeAccountIds(
        List<Integer> socialAccountIds
    ) {
        if (socialAccountIds == null
            || socialAccountIds.isEmpty()) {

            throw new BadRequestException(
                ErrorCode.INVALID_SOCIAL_ACCOUNT,
                "Phải chọn ít nhất một tài khoản mạng xã hội"
            );
        }

        if (socialAccountIds.contains(null)) {
            throw new BadRequestException(
                ErrorCode.INVALID_SOCIAL_ACCOUNT,
                "socialAccountIds không được chứa giá trị null"
            );
        }

        Set<Integer> uniqueIds =
            new LinkedHashSet<>(socialAccountIds);

        return List.copyOf(uniqueIds);
    }

    /**
     * Kiểm tra các tài khoản:
     * - tồn tại;
     * - thuộc user;
     * - đang có trạng thái hợp lệ để đăng bài.
     */
    private List<SocialAccount> getValidSocialAccounts(
        Integer userId,
        List<Integer> accountIds
    ) {
        List<SocialAccount> accounts =
            socialAccountRepository.findActiveAccountsByIds(
                userId,
                accountIds
            );

        if (accounts.size() != accountIds.size()) {
            throw new BadRequestException(
                ErrorCode.INVALID_SOCIAL_ACCOUNT,
                "Một hoặc nhiều tài khoản mạng xã hội "
                    + "không tồn tại, không hoạt động "
                    + "hoặc không thuộc người dùng"
            );
        }

        return accounts;
    }

    /**
     * Không cho đặt lịch trong quá khứ.
     */
    private void validateScheduledAt(Instant scheduledAt) {
        if (!scheduledAt.isAfter(Instant.now())) {
            throw new BadRequestException(
                ErrorCode.INVALID_REQUEST,
                "Thời gian lên lịch phải lớn hơn thời điểm hiện tại"
            );
        }
    }

    private void validatePostCanBeCancelled(Post post) {
        PostStatus status = post.getStatus();

        if (status == PostStatus.PUBLISHED) {
            throw new BadRequestException(
                ErrorCode.INVALID_POST_STATUS,
                "Không thể hủy bài đã đăng thành công"
            );
        }

        if (status == PostStatus.FAILED) {
            throw new BadRequestException(
                ErrorCode.INVALID_POST_STATUS,
                "Không thể hủy bài đã thất bại"
            );
        }

        if (status == PostStatus.CANCELLED) {
            throw new ConflictException(
                ErrorCode.INVALID_POST_STATUS,
                "Bài đăng đã được hủy trước đó"
            );
        }
    }

    /**
     * Chuyển Post thành payload Kafka và đóng gói
     * payload vào một bản ghi Outbox.
     */
    private OutBox buildPublishRequestedEvent(Post post) {
        PublishRequestedEvent payload =
            publishEventMapper.toPublishRequestedEvent(post);

        return outboxMapper.toPublishRequestedOutbox(
            post,
            payload,
            objectMapper
        );
    }
}
