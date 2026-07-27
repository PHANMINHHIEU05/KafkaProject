package com.example.dto;

import java.util.List;

public record CurrentUserContextResponse(
    UserInfo user,
    OrganizationMemberInfo organizationMember,
    OrganizationInfo organization,
    DepartmentInfo department,
    List<RoleInfo> roles,
    List<PermissionInfo> permissions
) {
    public record UserInfo(
        int id,
        String firstName,
        String lastName,
        String email,
        String status
    ) {
    }

    public record OrganizationMemberInfo(
        long id,
        String status
    ) {
    }

    public record OrganizationInfo(
        int id,
        String name,
        String slug,
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

    public record PermissionInfo(
        int id,
        String code,
        String name,
        String permissionGroup
    ) {
    }
}
