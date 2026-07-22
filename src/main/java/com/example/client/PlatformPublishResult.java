package com.example.client;

public record PlatformPublishResult(
    String externalPostId,
    String externalPostUrl,
    Integer httpStatusCode,
    String responseBody
) {
}