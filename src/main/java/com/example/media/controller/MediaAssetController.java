package com.example.media.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.media.dto.ConfirmMediaUploadResponse;
import com.example.media.dto.InitiateMediaUploadRequest;
import com.example.media.dto.InitiateMediaUploadResponse;
import com.example.media.dto.MediaAssetResponse;
import com.example.media.dto.MediaDownloadResponse;
import com.example.media.service.MediaAssetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/media-assets")
@RequiredArgsConstructor
public class MediaAssetController {
    private final MediaAssetService mediaAssetService;
    @PostMapping("/uploads")
    @PreAuthorize("hasAuthority('MEDIA_UPLOAD')")
    public ResponseEntity<InitiateMediaUploadResponse> initiateUpload(@Valid @RequestBody InitiateMediaUploadRequest request) {
        InitiateMediaUploadResponse response = mediaAssetService.initiateUpload(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ConfirmMediaUploadResponse> confirmUpload(@PathVariable Long id) {
        ConfirmMediaUploadResponse response = mediaAssetService.confirmUpload(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/{id}/download-url")
    public ResponseEntity<MediaDownloadResponse> getDownloadUrl(@PathVariable Long id) {
        MediaDownloadResponse downloadUrl = mediaAssetService.createDownloadUrl(id);
        return ResponseEntity.status(HttpStatus.OK).body(downloadUrl);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MEDIA_READ')")
    public ResponseEntity<Page<MediaAssetResponse>> getMediaAssets(Pageable pageable) {
        return ResponseEntity.ok(mediaAssetService.getVisibleMedia(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDIA_READ')")
    public ResponseEntity<MediaAssetResponse> getMediaAssetById(@PathVariable Long id) {
        return ResponseEntity.ok(mediaAssetService.getMediaAssetById(id));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('MEDIA_READ')")
    public ResponseEntity<Page<MediaAssetResponse>> getMyMedia(
            Pageable pageable
    ) {
        return ResponseEntity.ok(mediaAssetService.getMyMedia(pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDIA_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mediaAssetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
