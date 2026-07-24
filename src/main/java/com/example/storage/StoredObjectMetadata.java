package com.example.storage;

public record StoredObjectMetadata(
        long size,
        String etag,
        String versionId,
        String contentType
) {
}