package com.example.dto;

import com.example.entity.enums.PostStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PostResponse(

    UUID id,

    UUID userId,

    String title,

    String content,

    PostStatus status,

    String clientRequestId,

    Instant scheduledAt,

    Long version,

    Instant createdAt,

    Instant updatedAt,

    List<PostMediaResponse> media,

    List<PostTargetResponse> targets

) {
}