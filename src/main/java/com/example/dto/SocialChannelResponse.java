package com.example.dto;

public record SocialChannelResponse(
    int id,
    String platform,
    String accountName,
    String ownerEmail,
    String externalChannelId,
    String channelType,
    String channelName,
    boolean canPublish,
    String status
) {
}
