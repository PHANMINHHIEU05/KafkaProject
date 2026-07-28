package com.example.controller;

import com.example.dto.OrganizationResponse;
import com.example.service.DemoPermissionViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final DemoPermissionViewService demoPermissionViewService;

    @GetMapping("/current")
    @PreAuthorize("hasAuthority('ORGANIZATION_READ')")
    public OrganizationResponse getCurrentOrganization() {
        return demoPermissionViewService.getCurrentOrganization();
    }
}
