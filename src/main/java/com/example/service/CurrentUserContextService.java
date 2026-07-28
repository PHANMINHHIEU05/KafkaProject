package com.example.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.CurrentUserContextResponse;
import com.example.entity.OrganizationMember;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.entity.enums.OrganizationMemStatus;
import com.example.repository.OrganizationMemberRepository;
import com.example.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrentUserContextService {
    private final CurrentUserProvider currentUserProvider;
    private final OrganizationMemberRepository organizationMemberRepository;

        @Transactional(readOnly = true)
    public CurrentUserContextResponse getCurrentUserContext() {
        String email = currentUserProvider.getCurrentUserEmail();
        OrganizationMember member = organizationMemberRepository
                .findActiveByUserEmailWithRolesAndPermissions(
                        email,
                        OrganizationMemStatus.ACTIVE
                )
                .orElseThrow(() -> new AccessDeniedException(
                        "user is not an active member of any organization"
                ));

        return toResponse(member);
    }

    private CurrentUserContextResponse toResponse(OrganizationMember member) {
        User user = member.getUser();

        List<CurrentUserContextResponse.RoleInfo> roles = member.getRoles()
                .stream()
                .sorted(Comparator.comparing(Role::getName))
                .map(role -> new CurrentUserContextResponse.RoleInfo(
                        role.getId(),
                        role.getName(),
                        role.getDescription()
                ))
                .toList();

        List<CurrentUserContextResponse.PermissionInfo> permissions =
                collectPermissions(member);

        return new CurrentUserContextResponse(
                new CurrentUserContextResponse.UserInfo(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getStatus().name()
                ),
                new CurrentUserContextResponse.OrganizationMemberInfo(
                        member.getId(),
                        member.getStatus().name()
                ),
                new CurrentUserContextResponse.OrganizationInfo(
                        member.getOrganization().getId(),
                        member.getOrganization().getName(),
                        member.getOrganization().getSlug(),
                        member.getOrganization().getStatus().name()
                ),
                new CurrentUserContextResponse.DepartmentInfo(
                        member.getDepartment().getId(),
                        member.getDepartment().getName(),
                        member.getDepartment().getStatus().name()
                ),
                roles,
                permissions
        );
    }

    private List<CurrentUserContextResponse.PermissionInfo> collectPermissions(
            OrganizationMember member
    ) {
        Map<String, Permission> permissionsByCode = new LinkedHashMap<>();

        member.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .sorted(Comparator.comparing(Permission::getCode))
                .forEach(permission ->
                        permissionsByCode.putIfAbsent(
                                permission.getCode(),
                                permission
                        )
                );

        return permissionsByCode.values()
                .stream()
                .map(permission -> new CurrentUserContextResponse.PermissionInfo(
                        permission.getId(),
                        permission.getCode(),
                        permission.getName(),
                        permission.getPermissionGroup().name()
                ))
                .toList();
    }
}
