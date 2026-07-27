package com.example.dto;

import java.util.List;

import com.example.entity.OrganizationMember;

public record UserResponse (
    int id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String avatarUrl,
    String status,
    String createdAt,
    String updatedAt,
    List<SocialAccountResponse> socialAccounts,
    List<PostResponse> posts,
    OrganizationMember organizationMember
){
    
}
