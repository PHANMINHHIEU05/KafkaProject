package com.example.dto;

import com.example.entity.Platform;
import com.example.entity.enums.ConnectionStatus;

import java.time.Instant;
import java.util.UUID;

public record SocialAccountResponse(

    UUID id,

    UUID userId,

    Platform platform,

    String externalAccountId,

    String accountName,

    boolean active,

    ConnectionStatus connectionStatus,

    Instant connectedAt,

    Instant createdAt,

    Instant updatedAt

) {
}