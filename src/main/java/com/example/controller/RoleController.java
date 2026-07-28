package com.example.controller;

import com.example.dto.RoleSummaryResponse;
import com.example.service.DemoPermissionViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final DemoPermissionViewService demoPermissionViewService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public List<RoleSummaryResponse> getRoles() {
        return demoPermissionViewService.getRoles();
    }
}
