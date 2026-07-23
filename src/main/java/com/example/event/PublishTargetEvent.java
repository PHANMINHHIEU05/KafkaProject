package com.example.event;

import com.example.entity.enums.Platform;


public record PublishTargetEvent(
    Long postTargetId,
    Integer socialAccountId,
    Platform platform,
    String idempotencyKey
) {
}
