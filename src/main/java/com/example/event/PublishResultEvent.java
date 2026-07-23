package com.example.event;

import com.example.entity.enums.Platform;

import java.time.Instant;

public record PublishResultEvent(
    Long postId,
    Long postTargetId,
    Long publishAttemptId,
    Platform platform,
    boolean success,
    String externalPostId,
    String externalPostUrl,
    Integer httpStatusCode,
    String errorCode,
    String errorMessage,
    boolean retryable,
    Instant occurredAt
) {
}
