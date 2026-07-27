package com.example.service;

import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.entity.OrganizationMember;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final OrganizationMemberService organizationMemberService;
    @Transactional
    public Set<String> getCurrentPermissionCodes () {
        OrganizationMember currentMember = organizationMemberService.getCurrentMember();
        return currentMember.getRoles().stream()
        .flatMap(role -> role.getPermissions().stream())
        .map(permission -> permission.getCode())
        .collect(java.util.stream.Collectors.toSet());
    }

    @Transactional
    public boolean hasPermission(String permissionCode){
        Set<String> permissionCodes = getCurrentPermissionCodes();
        return permissionCodes.contains(permissionCode);
    }

    @Transactional
    public void requirePermission(String permissionCode){
        if (!hasPermission(permissionCode)) {
            throw new AccessDeniedException("Insufficient permissions");
        }
    }

    @Transactional
    public boolean hasRole(String roleName){
        OrganizationMember currentMember = organizationMemberService.getCurrentMember();
        return currentMember.getRoles().stream()
        .anyMatch(role -> role.getName().equals(roleName));
    }

    @Transactional
    void requireRole(String roleName){
        if(!hasRole(roleName)){
            throw new AccessDeniedException("Insufficient role");
        }
    }
}
