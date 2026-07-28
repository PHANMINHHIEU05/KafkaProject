package com.example.dto;

import java.util.List;

public record OrganizationMemberSummaryResponse(
    long id,
    String status,
    UserInfo user,
    DepartmentInfo department,
    List<RoleInfo> roles,
    List<String> permissionCodes
) {
    public record UserInfo(
        int id,
        String firstName,
        String lastName,
        String email,
        String status
    ) {
    }

    public record DepartmentInfo(
        int id,
        String name,
        String status
    ) {
    }

    public record RoleInfo(
        int id,
        String name,
        String description
    ) {
    }
}
