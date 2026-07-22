package com.example.event;

import java.util.UUID;

public record PublishMediaEvent(
    UUID mediaId,
    Object mediaType,
    String mediaUrl,
    String mimeType,
    String thumbnailUrl,
    Integer sortOrder
) {
}