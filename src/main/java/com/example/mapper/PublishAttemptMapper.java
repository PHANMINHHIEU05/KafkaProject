package com.example.mapper;

import com.example.dto.PublishAttemptResponse;
import com.example.entity.PostTarget;
import com.example.entity.PublishAttempt;
import com.example.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface PublishAttemptMapper {

    @Mapping(
        target = "postTargetId",
        source = "postTarget.id"
    )
    PublishAttemptResponse toResponse(
        PublishAttempt entity
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postTarget", source = "postTarget")
    @Mapping(target = "attemptNumber", source = "attemptNumber")
    @Mapping(target = "status", expression = "java(com.example.entity.enums.AttemptStatus.PROCESSING)")
    @Mapping(target = "requestId", source = "requestId")
    @Mapping(target = "httpStatusCode", ignore = true)
    @Mapping(target = "retryable", constant = "false")
    @Mapping(target = "errorCode", ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "startedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "finishedAt", ignore = true)
    PublishAttempt toProcessingAttempt(
        PostTarget postTarget,
        Integer attemptNumber,
        String requestId
    );
}
