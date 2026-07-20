package com.example.dto;

import java.util.UUID;

import com.example.entity.enums.MediaType;

public record PostMediaResponse(

    UUID id,

    MediaType mediaType,

    String mediaUrl,

    String mimeType,

    String thumbnailUrl,

    Integer sortOrder
) {
}