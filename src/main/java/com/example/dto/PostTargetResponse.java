package com.example.dto;

import com.example.entity.enums.Platform;
import com.example.entity.enums.PublishStatus;

import java.time.Instant;

public record PostTargetResponse(

    Long id,

    Long postId,

    Integer socialAccountId,

    String accountName,

    Platform platform,

    PublishStatus status,

    String externalPostId,

    String errorCode,

    String errorMessage,

    Instant processingStartedAt,

    Instant publishedAt
) {
}
