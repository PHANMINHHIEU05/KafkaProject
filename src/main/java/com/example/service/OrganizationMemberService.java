package com.example.service;


import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.entity.Organization;
import com.example.entity.OrganizationMember;
import com.example.entity.User;
import com.example.entity.enums.OrganizationMemStatus;
import com.example.repository.OrganizationMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationMemberService  {
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserService userService;
    public OrganizationMember getByUserId(Integer userId) {
        return organizationMemberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Organization member not found with user id: " + userId));
    }
    public OrganizationMember getActiveMemberByUserId(Integer userId) {
        return organizationMemberRepository.findByUserIdAndStatus(userId, OrganizationMemStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Active organization member not found with user id: " + userId));
    }
    public OrganizationMember getCurrentMember(){
        User currentUser = userService.getCurrentUser();
        return organizationMemberRepository.findByUserIdAndStatus(currentUser.getId(), OrganizationMemStatus.ACTIVE)
                .orElseThrow(() -> new AccessDeniedException("user is not an active member of any organization"));
    }
    public Organization getCurrentOrganization(){
        OrganizationMember currentMember = getCurrentMember();
        return currentMember.getOrganization();
    }
}
