package com.example.dto;

import com.example.entity.enums.OrganizationStatus;

public record CreateOrganizationRequest(
    String name,
    String slug,
    String description,
    String logoUrl,
    OrganizationStatus status
) {
    
}
