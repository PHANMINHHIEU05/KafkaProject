package com.example.media.dto;

import java.time.Instant;

public record MediaDownloadResponse(
        Long mediaAssetId,
        String downloadUrl,
        Instant expiresAt
) {
}