package com.example.dto;


import com.example.entity.enums.ConnectionStatus;
import com.example.entity.enums.Platform;

import java.time.Instant;

public record SocialAccountResponse(

    Integer id,

    Integer userId,

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
