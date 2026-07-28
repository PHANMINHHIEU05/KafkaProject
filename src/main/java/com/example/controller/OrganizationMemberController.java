package com.example.controller;

import com.example.dto.OrganizationMemberSummaryResponse;
import com.example.service.DemoPermissionViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/organization-members")
@RequiredArgsConstructor
public class OrganizationMemberController {
    private final DemoPermissionViewService demoPermissionViewService;

    @GetMapping
    @PreAuthorize("hasAuthority('ORGANIZATION_MEMBER_READ')")
    public List<OrganizationMemberSummaryResponse> getOrganizationMembers() {
        return demoPermissionViewService.getOrganizationMembers();
    }
}
