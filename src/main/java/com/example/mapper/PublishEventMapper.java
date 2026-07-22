package com.example.mapper;

import com.example.client.PlatformPublishResult;
import com.example.config.MapStructConfig;
import com.example.entity.Post;
import com.example.entity.PostMedia;
import com.example.entity.PostTarget;
import com.example.entity.PublishAttempt;
import com.example.entity.enums.Platform;
import com.example.event.PublishMediaEvent;
import com.example.event.PublishRequestedEvent;
import com.example.event.PublishResultEvent;
import com.example.event.PublishTargetEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface PublishEventMapper {

    @Mapping(target = "postId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    PublishRequestedEvent toPublishRequestedEvent(Post post);

    @Mapping(target = "mediaId", source = "id")
    @Mapping(
        target = "mediaType",
        expression = "java(media.getMediaType() == null ? null : media.getMediaType().name())"
    )
    PublishMediaEvent toPublishMediaEvent(PostMedia media);

    @Mapping(target = "postTargetId", source = "id")
    @Mapping(target = "socialAccountId", source = "socialAccount.id")
    PublishTargetEvent toPublishTargetEvent(PostTarget target);

    @Mapping(target = "postId", source = "event.postId")
    @Mapping(target = "postTargetId", source = "target.postTargetId")
    @Mapping(target = "publishAttemptId", source = "attempt.id")
    @Mapping(target = "platform", source = "platform")
    @Mapping(target = "success", constant = "true")
    @Mapping(target = "externalPostId", source = "result.externalPostId")
    @Mapping(target = "externalPostUrl", source = "result.externalPostUrl")
    @Mapping(target = "httpStatusCode", source = "result.httpStatusCode")
    @Mapping(target = "errorCode", expression = "java(null)")
    @Mapping(target = "errorMessage", expression = "java(null)")
    @Mapping(target = "retryable", constant = "false")
    @Mapping(target = "occurredAt", expression = "java(java.time.Instant.now())")
    PublishResultEvent toSuccessResultEvent(
        PublishRequestedEvent event,
        PublishTargetEvent target,
        PublishAttempt attempt,
        Platform platform,
        PlatformPublishResult result
    );

    @Mapping(target = "postId", source = "event.postId")
    @Mapping(target = "postTargetId", source = "target.postTargetId")
    @Mapping(target = "publishAttemptId", source = "attempt.id")
    @Mapping(target = "platform", source = "platform")
    @Mapping(target = "success", constant = "false")
    @Mapping(target = "externalPostId", expression = "java(null)")
    @Mapping(target = "externalPostUrl", expression = "java(null)")
    @Mapping(target = "httpStatusCode", expression = "java(null)")
    @Mapping(target = "errorCode", source = "errorCode")
    @Mapping(target = "errorMessage", source = "errorMessage")
    @Mapping(target = "retryable", source = "retryable")
    @Mapping(target = "occurredAt", expression = "java(java.time.Instant.now())")
    PublishResultEvent toFailureResultEvent(
        PublishRequestedEvent event,
        PublishTargetEvent target,
        PublishAttempt attempt,
        Platform platform,
        String errorCode,
        String errorMessage,
        boolean retryable
    );
}
