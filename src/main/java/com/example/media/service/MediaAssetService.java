package com.example.media.service;

import com.example.media.dto.ConfirmMediaUploadResponse;
import com.example.media.dto.InitiateMediaUploadRequest;
import com.example.media.dto.InitiateMediaUploadResponse;
import com.example.media.dto.MediaDownloadResponse;
import com.example.media.entity.MediaAsset;

import java.util.Collection;
import java.util.List;

public interface MediaAssetService {

    InitiateMediaUploadResponse initiateUpload(
            InitiateMediaUploadRequest request
    );

    ConfirmMediaUploadResponse confirmUpload(
            Long mediaAssetId
    );

    MediaDownloadResponse createDownloadUrl(
            Long mediaAssetId
    );

    List<MediaAsset> getReadyAssets(
            Collection<Long> mediaAssetIds,
            Integer organizationId
    );

    void delete(Long mediaAssetId);
}