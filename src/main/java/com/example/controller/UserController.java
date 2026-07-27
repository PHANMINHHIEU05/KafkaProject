package com.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.UserResponse;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@RequestMapping("/api/users")
@RestController()
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        User user = userService.getCurrentUser();
        return userMapper.toResponse(user);
    }
    
}
