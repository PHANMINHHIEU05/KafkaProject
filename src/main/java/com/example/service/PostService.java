package com.example.service;

import com.example.dto.CreatePostMediaRequest;
import com.example.dto.CreatePostRequest;
import com.example.dto.PostResponse;
import com.example.dto.PostSummaryResponse;
import com.example.entity.OutBox;
import com.example.entity.OrganizationMember;
import com.example.entity.Post;
import com.example.entity.PostMedia;
import com.example.entity.PostTarget;
import com.example.entity.SocialChannel;
import com.example.entity.User;
import com.example.entity.enums.ConnectionStatus;
import com.example.entity.enums.PostStatus;
import com.example.entity.enums.PublishStatus;
import com.example.entity.enums.SocialChannelStatus;
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
import com.example.repository.SocialChannelRepository;
import com.example.media.service.MediaAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private final SocialChannelRepository socialChannelRepository;
    private final PostRepository postRepository;
    private final OutboxService outboxService;
    private final OrganizationMemberService organizationMemberService;
    private final MediaAssetService mediaAssetService;
    private final AuthorizationService authorizationService;

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
        CreatePostRequest request
    ) {
        authorizationService.requirePermission("POST_CREATE");
        authorizationService.requirePermission("POST_PUBLISH");
        OrganizationMember currentMember = organizationMemberService.getCurrentMember();
        User user = currentMember.getUser();

        validateClientRequestId(
            currentMember.getOrganization().getId(),
            user.getId(),
            request.clientRequestId()
        );

        List<Integer> channelIds =
            normalizeChannelIds(request.socialChannelIds());

        List<SocialChannel> socialChannels =
            getValidSocialChannels(currentMember.getOrganization().getId(), channelIds);

        validateMediaAssets(currentMember.getOrganization().getId(), request.mediaList());

        Post post = buildPost(
            currentMember,
            user,
            request,
            socialChannels
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
        Long postId
    ) {
        authorizationService.requirePermission("POST_READ");
        OrganizationMember currentMember = organizationMemberService.getCurrentMember();

        Post post = postRepository
            .findByIdAndOrganizationId(postId, currentMember.getOrganization().getId())
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
        PostStatus status,
        Pageable pageable
    ) {
        authorizationService.requirePermission("POST_READ");
        OrganizationMember currentMember = organizationMemberService.getCurrentMember();
        Integer userId = currentMember.getUser().getId();

        Page<Post> posts;

        if (status == null) {
            posts = postRepository.findAllByOrgIdAndUserId(
                currentMember.getOrganization().getId(),
                userId,
                pageable
            );
        } else {
            posts = postRepository.findAllByOrgIdAndUserIdAndStatus(
                currentMember.getOrganization().getId(),
                userId,
                status,
                pageable
            );
        }

        return posts.map(postMapper::toSummaryResponse);
    }

    @Transactional
    public PostResponse cancelPost(
        Long postId
    ) {
        authorizationService.requirePermission("POST_CANCEL");
        OrganizationMember currentMember = organizationMemberService.getCurrentMember();

        Post post = postRepository
            .findByIdAndOrganizationId(postId, currentMember.getOrganization().getId())
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
        OrganizationMember currentMember,
        User user,
        CreatePostRequest request,
        List<SocialChannel> socialChannels
    ) {
        Post post = postMapper.toEntity(request);

        post.setOrganization(currentMember.getOrganization());
        post.setDepartment(currentMember.getDepartment());
        post.setUser(user);

        if (request.scheduledAt() == null) {
            post.setStatus(PostStatus.QUEUED);
        } else {
            validateScheduledAt(request.scheduledAt());
            post.setStatus(PostStatus.SCHEDULED);
        }

        addMedia(post, request.mediaList());
        addTargets(post, socialChannels);

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
     * Mỗi SocialChannel tạo một PostTarget.
     */
    private void addTargets(
        Post post,
        List<SocialChannel> socialChannels
    ) {
        for (SocialChannel channel : socialChannels) {
            PostTarget target = postTargetMapper.toEntity(channel);

            /*
             * addTarget phải đồng thời thực hiện:
             * post.getTargets().add(target);
             * target.setPost(post);
             */
            post.addTarget(target);
        }
    }

    /**
     * Chống việc client gửi lại cùng một request tạo bài.
     */
    private void validateClientRequestId(
        Integer orgId,
        Integer userId,
        String clientRequestId
    ) {
        if (clientRequestId == null
            || clientRequestId.isBlank()) {
            return;
        }

        boolean existed =
            postRepository.existsByOrgId(
                orgId,
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
     * Kiểm tra và loại bỏ socialChannelId bị trùng.
     */
    private List<Integer> normalizeChannelIds(
        List<Integer> socialChannelIds
    ) {
        if (socialChannelIds == null
            || socialChannelIds.isEmpty()) {

            throw new BadRequestException(
                ErrorCode.INVALID_SOCIAL_ACCOUNT,
                "Phải chọn ít nhất một kênh đăng"
            );
        }

        if (socialChannelIds.contains(null)) {
            throw new BadRequestException(
                ErrorCode.INVALID_SOCIAL_ACCOUNT,
                "socialChannelIds không được chứa giá trị null"
            );
        }

        Set<Integer> uniqueIds =
            new LinkedHashSet<>(socialChannelIds);

        return List.copyOf(uniqueIds);
    }

    /**
     * Kiểm tra các kênh:
     * - tồn tại;
     * - thuộc organization hiện tại;
     * - đang có trạng thái hợp lệ để đăng bài.
     */
    private List<SocialChannel> getValidSocialChannels(
        Integer orgId,
        List<Integer> channelIds
    ) {
        List<SocialChannel> channels =
            socialChannelRepository.findPublishChannels(
                channelIds,
                orgId,
                SocialChannelStatus.ACTIVE,
                ConnectionStatus.CONNECTED
            );

        if (channels.size() != channelIds.size()) {
            throw new BadRequestException(
                ErrorCode.INVALID_SOCIAL_ACCOUNT,
                "Một hoặc nhiều kênh đăng không tồn tại, không thuộc organization hoặc không thể publish"
            );
        }

        return channels;
    }

    private void validateMediaAssets(
        Integer orgId,
        List<CreatePostMediaRequest> mediaRequests
    ) {
        if (mediaRequests == null || mediaRequests.isEmpty()) {
            return;
        }

        List<Long> mediaAssetIds = mediaRequests.stream()
            .map(CreatePostMediaRequest::mediaAssetId)
            .toList();

        if (mediaAssetIds.contains(null)) {
            throw new BadRequestException(
                ErrorCode.MEDIA_VALIDATION_ERROR,
                "mediaAssetId không được chứa giá trị null"
            );
        }

        long distinctCount = new LinkedHashSet<>(mediaAssetIds).size();
        int readyCount = mediaAssetService.getReadyAssets(mediaAssetIds, orgId).size();

        if (readyCount != distinctCount) {
            throw new BadRequestException(
                ErrorCode.MEDIA_VALIDATION_ERROR,
                "Một hoặc nhiều media asset không tồn tại, chưa READY hoặc không thuộc organization"
            );
        }
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
