package com.example.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dto.CreatePostRequest;
import com.example.dto.PostResponse;
import com.example.dto.PostSummaryResponse;
import com.example.entity.enums.PostStatus;
import com.example.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/post/")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestHeader("x-user-id") UUID userId, @Valid @RequestBody CreatePostRequest request) {
        PostResponse response = postService.createPost(userId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@RequestHeader("x-user-id")UUID userId , @PathVariable("postId") UUID postId) {
        PostResponse response = postService.getPostById(userId, postId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping()
    public ResponseEntity<Page<PostSummaryResponse>> getPosts(@RequestHeader("x-user-id") UUID userId , @RequestParam(required = false) PostStatus status , @RequestParam(required = false , defaultValue = "0") int page  , @RequestParam(required = false , defaultValue = "10") int size) {
        var response = postService.getPosts(userId, status,PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

     @PatchMapping("/{postId}/cancel")
    public ResponseEntity<PostResponse> cancelPost(
        @RequestHeader("X-User-Id") UUID userId,
        @PathVariable UUID postId
    ) {
        return ResponseEntity.ok(
            postService.cancelPost(userId, postId)
        );
    }
}
