package com.example.event;


public record PublishMediaEvent(
    Long mediaId,
    Object mediaType,
    String mediaUrl,
    String mimeType,
    String thumbnailUrl,
    Integer sortOrder
) {
}