package com.example.event;

import com.example.entity.enums.Platform;

import java.util.UUID;

public record PublishTargetEvent(
    UUID postTargetId,
    UUID socialAccountId,
    Platform platform,
    String idempotencyKey
) {
}