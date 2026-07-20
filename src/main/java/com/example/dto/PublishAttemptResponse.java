package com.example.dto;

import java.time.Instant;
import java.util.UUID;

import com.example.entity.enums.AttemptStatus;

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
