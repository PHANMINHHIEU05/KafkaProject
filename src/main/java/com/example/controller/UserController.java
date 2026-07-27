package com.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.example.dto.UserResponse;
import com.example.dto.CurrentUserContextResponse;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.CurrentUserContextService;
import com.example.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@RequestMapping("/api/users")
@RestController()
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final CurrentUserContextService currentUserContextService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UserResponse getCurrentUser() {
        User user = userService.getCurrentUser();
        return userMapper.toResponse(user);
    }

    @GetMapping("/me/context")
    @PreAuthorize("isAuthenticated()")
    public CurrentUserContextResponse getCurrentUserContext() {
        return currentUserContextService.getCurrentUserContext();
    }
    
}
