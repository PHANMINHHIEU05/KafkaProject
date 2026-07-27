package com.example.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.entity.Role;
import com.example.exception.ErrorCode;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final OrganizationMemberService organizationMemberService;

    public Set<Role> getByIds(Set<Integer> ids){                                                                                                                                                             
        var currentMember = organizationMemberService.getCurrentMember();
        Set<Role> roles = roleRepository.findActiveByIdsInScope(
            ids,
            currentMember.getOrganization().getId(),
            currentMember.getDepartment().getId()
        );
        if (roles.isEmpty()) {
            throw new ResourceNotFoundException(ErrorCode.ROLES_NOT_FOUND, "Roles not found for ids: " + ids);
        }
        if (roles.size() != ids.size()) {
            throw new ResourceNotFoundException(ErrorCode.ROLES_NOT_FOUND, "Some roles are not in current organization/department scope: " + ids);
        }
        return roles;
    }
}
