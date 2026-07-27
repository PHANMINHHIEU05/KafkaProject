package com.example.auth.dto;

import java.time.Instant;

public record AuthResponse(
    String tokenType,
    String accessToken,
    Instant expiresAt
) {
}
