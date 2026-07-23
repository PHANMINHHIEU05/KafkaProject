package com.example.dto;

import java.time.Instant;
import com.example.entity.enums.AttemptStatus;

public record PublishAttemptResponse (
    Long id,

    Long postTargetId,

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
