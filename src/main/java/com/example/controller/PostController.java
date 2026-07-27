package com.example.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.CreatePostRequest;
import com.example.dto.PostResponse;
import com.example.dto.PostSummaryResponse;
import com.example.entity.enums.PostStatus;
import com.example.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/post/")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    @PostMapping
    @PreAuthorize("hasAuthority('POST_CREATE') and hasAuthority('POST_PUBLISH')")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        PostResponse response = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{postId}")
    @PreAuthorize("hasAuthority('POST_READ')")
    public ResponseEntity<PostResponse> getPostById(@PathVariable("postId") Long postId) {
        PostResponse response = postService.getPostById(postId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('POST_READ')")
    public ResponseEntity<Page<PostSummaryResponse>> getPosts(@RequestParam(required = false) PostStatus status , @RequestParam(required = false , defaultValue = "0") int page  , @RequestParam(required = false , defaultValue = "10") int size) {
        var response = postService.getPosts(status,PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

     @PatchMapping("/{postId}/cancel")
     @PreAuthorize("hasAuthority('POST_CANCEL')")
    public ResponseEntity<PostResponse> cancelPost(
        @PathVariable Long postId
    ) {
        return ResponseEntity.ok(
            postService.cancelPost(postId)
        );
    }
}
