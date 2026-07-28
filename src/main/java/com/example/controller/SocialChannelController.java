package com.example.controller;

import com.example.dto.SocialChannelResponse;
import com.example.service.DemoPermissionViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/social-channels")
@RequiredArgsConstructor
public class SocialChannelController {
    private final DemoPermissionViewService demoPermissionViewService;

    @GetMapping
    @PreAuthorize("hasAuthority('SOCIAL_CHANNEL_READ')")
    public List<SocialChannelResponse> getSocialChannels() {
        return demoPermissionViewService.getSocialChannels();
    }
}
