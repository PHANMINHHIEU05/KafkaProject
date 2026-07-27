package com.example.mapper;

import java.time.Instant;

import com.example.config.MapStructConfig;
import com.example.entity.Department;
import com.example.entity.Organization;
import com.example.entity.User;
import com.example.media.dto.ConfirmMediaUploadResponse;
import com.example.media.dto.InitiateMediaUploadRequest;
import com.example.media.dto.MediaAssetResponse;
import com.example.media.dto.MediaDownloadResponse;
import com.example.media.entity.MediaAsset;
import com.example.media.entity.MediaUploadStatus;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface MediaAssetMapper {

    @Mapping(target = "mediaAssetId", source = "id")
    ConfirmMediaUploadResponse toConfirmMediaUploadResponse(MediaAsset mediaAsset);

    @Mapping(target = "mediaAssetId", source = "mediaAsset.id")
    @Mapping(target = "downloadUrl", source = "downloadUrl")
    @Mapping(target = "expiresAt", source = "expiresAt")
    MediaDownloadResponse toDownloadResponse(
            MediaAsset mediaAsset,
            String downloadUrl,
            Instant expiresAt
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organization", source = "organization")
    @Mapping(target = "department", source = "department")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "bucketName", source = "bucketName")
    @Mapping(target = "objectKey", source = "objectKey")
    @Mapping(target = "originalFilename", source = "request.originalFilename")
    @Mapping(target = "mediaType", source = "request.mediaType")
    @Mapping(target = "mimeType", source = "mimeType")
    @Mapping(target = "sizeBytes", source = "request.sizeBytes")
    @Mapping(target = "checksumSha256", source = "checksumSha256")
    @Mapping(target = "uploadStatus", source = "uploadStatus")
    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "objectVersionId", ignore = true)
    @Mapping(target = "confirmedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "postMedia", ignore = true)
    MediaAsset toUploadingMediaAsset(
            InitiateMediaUploadRequest request,
            Organization organization,
            Department department,
            User user,
            String bucketName,
            String objectKey,
            String mimeType,
            String checksumSha256,
            MediaUploadStatus uploadStatus
    );

    @Mapping(target = "id", source = "mediaAsset.id")
    @Mapping(target = "originalFilename", source = "mediaAsset.originalFilename")
    @Mapping(target = "mediaType", source = "mediaAsset.mediaType")
    @Mapping(target = "mimeType", source = "mediaAsset.mimeType")
    @Mapping(target = "sizeBytes", source = "mediaAsset.sizeBytes")
    @Mapping(target = "uploadStatus", source = "mediaAsset.uploadStatus")
    @Mapping(target = "departmentId", source = "mediaAsset.department.id")
    @Mapping(target = "departmentName", source = "mediaAsset.department.name")
    @Mapping(target = "uploadedById", source = "mediaAsset.user.id")
    @Mapping(target = "uploadedByName", expression = "java(buildUploadedByName(mediaAsset.getUser()))")
    @Mapping(target = "createdAt", source = "mediaAsset.createdAt")
    @Mapping(target = "confirmedAt", source = "mediaAsset.confirmedAt")
    MediaAssetResponse toResponse(MediaAsset mediaAsset);

    default String buildUploadedByName(User user) {
        if (user == null) {
            return null;
        }

        String firstName = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String lastName = user.getLastName() == null ? "" : user.getLastName().trim();
        String fullName = (firstName + " " + lastName).trim();

        return fullName.isBlank() ? user.getEmail() : fullName;
    }
}
