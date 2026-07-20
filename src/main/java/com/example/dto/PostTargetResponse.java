package com.example.dto;

import com.example.entity.Platform;
import com.example.entity.enums.PublishStatus;

import java.time.Instant;
import java.util.UUID;

public record PostTargetResponse(

    UUID id,

    UUID postId,

    UUID socialAccountId,

    String accountName,

    Platform platform,

    PublishStatus status,

    String externalPostId,

    String ErrorCode,

    String ErrorMessage,

    Instant processingStartedAt,

    Instant publishedAt
) {
}