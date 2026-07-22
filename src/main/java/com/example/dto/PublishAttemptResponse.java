package com.example.dto;

import java.time.Instant;
import com.example.entity.enums.AttemptStatus;
import java.util.UUID;

public record PublishAttemptResponse (
    Long id,

    UUID postTargetId,

    Integer attemptNumber,

    AttemptStatus status,

    String requestId,

    Integer httpStatusCode,

    boolean retryable,

    String errorCode,

    String errorMessage,

    Instant startedAt,

    Instant finishedAt
) {
    
}
