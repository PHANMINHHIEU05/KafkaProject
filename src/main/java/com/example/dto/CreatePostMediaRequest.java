package com.example.dto;

import com.example.entity.enums.MediaType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePostMediaRequest(

    @NotNull(message = "Loại media không được để trống")
    MediaType mediaType,

    @NotBlank(message = "Media URL không được để trống")
    String mediaUrl,

    String mimeType,

    String thumbnailUrl,

    Integer sortOrder

) {
}