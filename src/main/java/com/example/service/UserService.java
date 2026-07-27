package com.example.service;

import org.springframework.stereotype.Service;

import com.example.entity.User;
import com.example.exception.ErrorCode;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.UserRepository;
import com.example.security.SecurityCurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SecurityCurrentUserProvider securityCurrentUserProvider;

    public User getUserById(Integer userId){
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND , "User with id " + userId + " not found"));
        return user;
    }

    public User getUserByEmail(String email){
        User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND , "User with email " + email + " not found"));
        return user;
    }

    public User getCurrentUser(){
        String email = securityCurrentUserProvider.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND , "User with email " + email + " not found"));
        return user;
    }

}
