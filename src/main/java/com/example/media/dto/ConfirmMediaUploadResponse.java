package com.example.media.dto;

import com.example.entity.enums.MediaUploadStatus;

import java.time.Instant;

public record ConfirmMediaUploadResponse(
        Long mediaAssetId,
        MediaUploadStatus uploadStatus,
        String etag,
        Long sizeBytes,
        String mimeType,
        Instant confirmedAt
) {
}
