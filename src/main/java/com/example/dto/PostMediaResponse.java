package com.example.dto;

import com.example.entity.enums.MediaType;

public record PostMediaResponse(

    Long id,

    Long mediaAssetId,

    MediaType mediaType,

    String mimeType,

    Integer sortOrder
) {
}
