package com.example.dto;

import java.util.List;

public record RoleSummaryResponse(
    int id,
    String name,
    String description,
    boolean active,
    DepartmentInfo department,
    List<PermissionInfo> permissions
) {
    public record DepartmentInfo(
        int id,
        String name
    ) {
    }

    public record PermissionInfo(
        int id,
        String code,
        String name,
        String permissionGroup
    ) {
    }
}
