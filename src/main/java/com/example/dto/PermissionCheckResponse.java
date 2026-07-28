package com.example.dto;

public record PermissionCheckResponse(
    String permissionCode,
    boolean allowed
) {
}
