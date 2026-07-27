package com.example.media.dto;

import java.time.Instant;
import java.util.Map;

public record InitiateMediaUploadResponse (
    Long mediaAssetId,
    String uploadUrl,
    String httpMethod,
    Map<String, String> requiredHeaders,
    Instant expiresAt
){
}
