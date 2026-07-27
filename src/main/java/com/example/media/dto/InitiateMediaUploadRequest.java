package com.example.media.dto;

import com.example.entity.enums.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record InitiateMediaUploadRequest(

        @NotBlank
        @Size(max = 255)
        String originalFilename,

        @NotNull
        MediaType mediaType,

        @NotBlank
        @Size(max = 100)
        String mimeType,

        @NotNull
        @Positive
        Long sizeBytes,

        @Size(max = 64)
        String checksumSha256

) {
}
