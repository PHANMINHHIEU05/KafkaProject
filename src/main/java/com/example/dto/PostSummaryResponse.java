package com.example.dto;

import com.example.entity.enums.PostStatus;

import java.time.Instant;
import java.util.UUID;

// postResponse nhưng chỉ trả ra thông tin cơ bản cho post thôi chứ k có post media hay post target
public record PostSummaryResponse(

    UUID id,

    String title,

    String content,

    PostStatus status,

    String clientRequestId,

    Instant scheduledAt,

    long targetCount,

    long publishedTargetCount,

    long failedTargetCount,

    Instant createdAt,

    Instant updatedAt

) {
}