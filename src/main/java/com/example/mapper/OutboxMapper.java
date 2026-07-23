package com.example.mapper;

import com.example.config.MapStructConfig;
import com.example.entity.OutBox;
import com.example.entity.Post;
import com.example.event.PublishRequestedEvent;
import com.example.event.PublishResultEvent;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tools.jackson.databind.ObjectMapper;

@Mapper(config = MapStructConfig.class)
public interface OutboxMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aggregateId", source = "post.id")
    @Mapping(target = "aggregateType", constant = "POST")
    @Mapping(target = "eventType", constant = "POST_PUBLISH_REQUESTED")
    @Mapping(target = "topic", constant = "post-publish-requests")
    @Mapping(target = "eventKey", expression = "java(post.getId().toString())")
    @Mapping(target = "payload", expression = "java(objectMapper.valueToTree(event))")
    @Mapping(target = "status", expression = "java(com.example.entity.enums.OutboxStatus.NEW)")
    @Mapping(target = "retryCount", constant = "0")
    @Mapping(target = "maxRetry", constant = "10")
    @Mapping(
        target = "availableAt",
        expression = "java(post.getScheduledAt() == null ? java.time.Instant.now() : post.getScheduledAt())"
    )
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "errorCode", ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    OutBox toPublishRequestedOutbox(
        Post post,
        PublishRequestedEvent event,
        @Context ObjectMapper objectMapper
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aggregateId", source = "result.postId")
    @Mapping(target = "aggregateType", constant = "POST")
    @Mapping(target = "eventType", constant = "POST_PUBLISH_RESULT")
    @Mapping(target = "topic", constant = "post-publish-results")
    @Mapping(target = "eventKey", expression = "java(result.postId().toString())")
    @Mapping(target = "payload", expression = "java(objectMapper.valueToTree(result))")
    @Mapping(target = "status", expression = "java(com.example.entity.enums.OutboxStatus.NEW)")
    @Mapping(target = "retryCount", constant = "0")
    @Mapping(target = "maxRetry", constant = "10")
    @Mapping(target = "availableAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "errorCode", ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    OutBox toPublishResultOutbox(
        PublishResultEvent result,
        @Context ObjectMapper objectMapper
    );
}
