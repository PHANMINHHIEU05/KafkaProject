package com.example.event;

import com.example.entity.enums.Platform;

import java.time.Instant;
import java.util.UUID;

public record PublishResultEvent(
    UUID postId,
    UUID postTargetId,
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
