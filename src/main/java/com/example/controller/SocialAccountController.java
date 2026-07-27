package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.dto.CreateSocialAccountRequest;
import com.example.dto.SocialAccountResponse;
import com.example.service.SocialAccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/social-account")
@RequiredArgsConstructor
public class SocialAccountController {
    private final SocialAccountService socialAccountService;
    
    @PostMapping
    @PreAuthorize("hasAuthority('SOCIAL_ACCOUNT_CONNECT')")
    public ResponseEntity<SocialAccountResponse> createSocialAccount(
        @RequestBody CreateSocialAccountRequest request
    ) {
        SocialAccountResponse response = socialAccountService.createSocialAccount(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SOCIAL_ACCOUNT_READ')")
    public ResponseEntity<List<SocialAccountResponse>> getSocialAccounts() {
        var response = socialAccountService.findActiveAccounts();
        return ResponseEntity.ok(response);
    }
    @PatchMapping
    @PreAuthorize("hasAuthority('SOCIAL_ACCOUNT_DISCONNECT')")
    public ResponseEntity<Void> disconnectSocialAccounts(
        @RequestParam Integer accountIds
    ) {
        socialAccountService.disconnectSocialAccount(accountIds);
        return ResponseEntity.ok().build();
    }
}
