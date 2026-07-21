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
import com.example.exception.BadRequestException;
import com.example.exception.ConflictException;
import com.example.exception.ErrorCode;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.PostMapper;
import com.example.mapper.PostMediaMapper;
import com.example.repository.OutboxEventRepository;
import com.example.repository.PostRepository;
import com.example.repository.SocialAccountRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final String PUBLISH_REQUEST_TOPIC =
        "post-publish-requests";

    private static final String PUBLISH_REQUEST_EVENT_TYPE =
        "POST_PUBLISH_REQUESTED";

    private static final String CANCEL_REQUEST_EVENT_TYPE =
        "POST_CANCEL_REQUESTED";

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final PostRepository postRepository;
    private final OutboxEventRepository outboxEventRepository;

    private final PostMapper postMapper;
    private final PostMediaMapper postMediaMapper;

    private final ObjectMapper objectMapper;

    /**
     * Tạo bài đăng mới.
     *
     * Post, PostMedia, PostTarget và OutboxEvent được lưu
     * trong cùng một transaction.
     */
    @Transactional
    public PostResponse createPost(
        UUID userId,
        CreatePostRequest request
    ) {
        User user = getUserOrThrow(userId);

        validateClientRequestId(
            userId,
            request.clientRequestId()
        );

        List<UUID> accountIds =
            normalizeAccountIds(request.socialAccountIds());

        List<SocialAccount> socialAccounts =
            getValidSocialAccounts(userId, accountIds);

        Post post = buildPost(
            user,
            request,
            socialAccounts
        );

        /*
         * Vì Post có cascade tới PostMedia và PostTarget,
         * lưu Post sẽ lưu luôn các entity con.
         */
        Post savedPost = postRepository.save(post);

        /*
         * Có thể gọi flush để chắc chắn Post và PostTarget
         * đã được INSERT trước khi tạo payload Outbox.
         */
        postRepository.flush();

        OutBox outboxEvent =
            buildPublishRequestedEvent(savedPost);

        outboxEventRepository.save(outboxEvent);

        return postMapper.toResponse(savedPost);
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(
        UUID userId,
        UUID postId
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
    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getPosts(
        UUID userId,
        PostStatus status,
        Pageable pageable
    ) {
        getUserOrThrow(userId);

        return postRepository
            .findAllByUserIdAndStatus(
                userId,
                status,
                pageable
            )
            .map(postMapper::toSummaryResponse);
    }
    @Transactional
    public PostResponse cancelPost(
        UUID userId,
        UUID postId
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

        boolean hasProcessingTarget = false;

        for (PostTarget target : post.getTargets()) {
            if (target.getStatus() == PublishStatus.PROCESSING) {
                hasProcessingTarget = true;
            }

            if (target.getStatus() == PublishStatus.PENDING) {
                target.setStatus(PublishStatus.CANCELLED);
            }
        }

        post.setStatus(PostStatus.CANCELLED);

        /*
         * Nếu worker có thể đang xử lý target, tạo event hủy
         * để các worker biết yêu cầu dừng.
         */
        if (hasProcessingTarget) {
            OutBox cancelEvent =
                buildCancelRequestedEvent(post);

            outboxEventRepository.save(cancelEvent);
        }

        /*
         * Không bắt buộc gọi save vì Post đang managed.
         * Khi transaction commit, Hibernate dirty checking
         * sẽ tự UPDATE.
         */
        return postMapper.toResponse(post);
    }

    /**
     * Tạo Entity Post và các entity con.
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
     * Chuyển danh sách CreatePostMediaRequest thành PostMedia.
     */
    @SuppressWarnings("unused")
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
             * addMedia vừa thêm vào list vừa gọi:
             * media.setPost(post)
             */
            post.addMedia(media);
        }
    }

    /**
     * Mỗi SocialAccount tạo ra một PostTarget.
     */
    private void addTargets(
        Post post,
        List<SocialAccount> socialAccounts
    ) {
        for (SocialAccount account : socialAccounts) {
            PostTarget target = PostTarget.builder()
                .socialAccount(account)
                .platform(account.getPlatform())
                .status(PublishStatus.PENDING)
                .idempotencyKey(UUID.randomUUID().toString())
                .build();

            /*
             * addTarget vừa thêm vào list vừa gọi:
             * target.setPost(post)
             */
            post.addTarget(target);
        }
    }

    /**
     * Kiểm tra user tồn tại.
     */
    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    ErrorCode.USER_NOT_FOUND,
                    "Không tìm thấy người dùng có id: " + userId
                )
            );
    }

    /**
     * Chống client gửi trùng cùng một yêu cầu tạo bài.
     */
    private void validateClientRequestId(
        UUID userId,
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
     * Loại bỏ ID bị lặp do client gửi lên.
     */
    private List<UUID> normalizeAccountIds(
        List<UUID> socialAccountIds
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

        Set<UUID> uniqueIds =
            new LinkedHashSet<>(socialAccountIds);

        return List.copyOf(uniqueIds);
    }

    /**
     * Kiểm tra các SocialAccount:
     * - tồn tại;
     * - thuộc user hiện tại;
     * - đang active.
     */
    private List<SocialAccount> getValidSocialAccounts(
        UUID userId,
        List<UUID> accountIds
    ) {
        List<SocialAccount> accounts =
            socialAccountRepository
                .findActiveAccountsByIds(
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
     * Không cho lên lịch trong quá khứ.
     */
    private void validateScheduledAt(Instant scheduledAt) {
        if (scheduledAt.isBefore(Instant.now())) {
            throw new BadRequestException(
                ErrorCode.INVALID_REQUEST,
                "Thời gian lên lịch phải lớn hơn thời điểm hiện tại"
            );
        }
    }

    /**
     * Kiểm tra trạng thái Post trước khi hủy.
     */
    private void validatePostCanBeCancelled(Post post) {
        PostStatus status = post.getStatus();

        if (status == PostStatus.PUBLISHED) {
            throw new BadRequestException(
                ErrorCode.INVALID_POST_STATUS,
                "Không thể hủy bài đã được đăng thành công"
            );
        }

        if (status == PostStatus.FAILED) {
            throw new BadRequestException(
                ErrorCode.INVALID_POST_STATUS,
                "Không thể hủy bài đã thất bại hoàn toàn"
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
     * Tạo Outbox event yêu cầu publish Post.
     */
    private OutBox buildPublishRequestedEvent(Post post) {
        PublishRequestedPayload payload =
            new PublishRequestedPayload(
                post.getId(),
                post.getUser().getId(),
                post.getTitle(),
                post.getContent(),
                post.getScheduledAt(),
                post.getMedia()
                    .stream()
                    .map(media ->
                        new PublishMediaPayload(
                            media.getId(),
                            media.getMediaType(),
                            media.getMediaUrl(),
                            media.getMimeType(),
                            media.getThumbnailUrl(),
                            media.getSortOrder()
                        )
                    )
                    .toList(),
                post.getTargets()
                    .stream()
                    .map(target ->
                        new PublishTargetPayload(
                            target.getId(),
                            target.getSocialAccount().getId(),
                            target.getPlatform(),
                            target.getIdempotencyKey()
                        )
                    )
                    .toList()
            );

        JsonNode payloadJson =
            objectMapper.valueToTree(payload);

        return OutBox.builder()
            .aggregateId(post.getId())
            .eventType(PUBLISH_REQUEST_EVENT_TYPE)
            .topic(PUBLISH_REQUEST_TOPIC)
            .eventKey(post.getId().toString())
            .payload(payloadJson)
            .status(OutboxStatus.NEW)
            .retryCount(0)
            .maxRetry(10)
            .availableAt(
                post.getScheduledAt() == null
                    ? Instant.now()
                    : post.getScheduledAt()
            )
            .build();
    }

    /**
     * Tạo event yêu cầu hủy.
     */
    private OutBox buildCancelRequestedEvent(Post post) {
        CancelRequestedPayload payload =
            new CancelRequestedPayload(
                post.getId(),
                post.getTargets()
                    .stream()
                    .filter(target ->
                        target.getStatus()
                            == PublishStatus.PROCESSING
                    )
                    .map(PostTarget::getId)
                    .toList()
            );

        return OutBox.builder()
            .aggregateId(post.getId())
            .eventType(CANCEL_REQUEST_EVENT_TYPE)
            .topic(PUBLISH_REQUEST_TOPIC)
            .eventKey(post.getId().toString())
            .payload(objectMapper.valueToTree(payload))
            .status(OutboxStatus.NEW)
            .retryCount(0)
            .maxRetry(10)
            .availableAt(Instant.now())
            .build();
    }

    private record PublishRequestedPayload(
        UUID postId,
        UUID userId,
        String title,
        String content,
        Instant scheduledAt,
        List<PublishMediaPayload> media,
        List<PublishTargetPayload> targets
    ) {
    }

    private record PublishMediaPayload(
        UUID mediaId,
        Object mediaType,
        String mediaUrl,
        String mimeType,
        String thumbnailUrl,
        Integer sortOrder
    ) {
    }

    private record PublishTargetPayload(
        UUID postTargetId,
        UUID socialAccountId,
        Object platform,
        String idempotencyKey
    ) {
    }

    private record CancelRequestedPayload(
        UUID postId,
        List<UUID> processingTargetIds
    ) {
    }
}