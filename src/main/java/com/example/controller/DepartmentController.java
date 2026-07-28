package com.example.controller;

import com.example.dto.DepartmentResponse;
import com.example.service.DemoPermissionViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DemoPermissionViewService demoPermissionViewService;

    @GetMapping
    @PreAuthorize("hasAuthority('DEPARTMENT_READ')")
    public List<DepartmentResponse> getDepartments() {
        return demoPermissionViewService.getDepartments();
    }
}
