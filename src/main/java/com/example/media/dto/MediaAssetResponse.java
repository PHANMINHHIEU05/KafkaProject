package com.example.media.dto;

import com.example.entity.enums.MediaType;
import com.example.media.entity.MediaUploadStatus;

import java.time.Instant;

public record MediaAssetResponse(

        Long id,

        String originalFilename,

        MediaType mediaType,

        String mimeType,

        Long sizeBytes,

        MediaUploadStatus uploadStatus,

        Integer departmentId,

        String departmentName,

        Integer uploadedById,

        String uploadedByName,

        Instant createdAt,

        Instant confirmedAt

) {
}
