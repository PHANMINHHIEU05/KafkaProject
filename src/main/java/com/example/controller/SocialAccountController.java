package com.example.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.dto.CreateSocialAccountRequest;
import com.example.dto.SocialAccountResponse;
import com.example.service.SocialAccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/api/social-account")
@RequiredArgsConstructor
public class SocialAccountController {
    private final SocialAccountService socialAccountService;
    
    @PostMapping
    public ResponseEntity<SocialAccountResponse> createSocialAccount(
        @RequestHeader("x-user-id") UUID  userId,
        @RequestBody CreateSocialAccountRequest request
    ) {
        SocialAccountResponse response = socialAccountService.createSocialAccount(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SocialAccountResponse>> getSocialAccounts(
        @RequestHeader("x-user-id") UUID userId
    ) {
        var response = socialAccountService.findActiveAccounts(userId);
        return ResponseEntity.ok(response);
    }
    @PatchMapping
    public ResponseEntity<Void> disconnectSocialAccounts(
        @RequestHeader("x-user-id") UUID userId,
        @RequestParam UUID accountIds
    ) {
        socialAccountService.disconnectSocialAccount(userId, accountIds);
        return ResponseEntity.ok().build();
    }
}
