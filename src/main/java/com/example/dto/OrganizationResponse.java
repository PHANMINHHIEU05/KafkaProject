package com.example.dto;

import java.time.Instant;

import com.example.entity.enums.OrganizationStatus;

public record OrganizationResponse(
    int id,
    String name,
    String slug,
    String description,
    String logoUrl,
    OrganizationStatus status,
    Instant createdAt,
    Instant updatedAt
) {
    
}
