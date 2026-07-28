package com.example.controller;

import com.example.dto.PermissionCheckResponse;
import com.example.service.DemoPermissionViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final DemoPermissionViewService demoPermissionViewService;

    @GetMapping("/check")
    @PreAuthorize("isAuthenticated()")
    public PermissionCheckResponse checkCurrentPermission(
        @RequestParam String code
    ) {
        return demoPermissionViewService.checkCurrentPermission(code);
    }
}
