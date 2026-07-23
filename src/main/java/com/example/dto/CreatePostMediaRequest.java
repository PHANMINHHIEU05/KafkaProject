package com.example.dto;

import jakarta.validation.constraints.NotNull;

public record CreatePostMediaRequest(

    @NotNull(message = "Media asset không được để trống")
    Long mediaAssetId,

    Integer sortOrder

) {
}
