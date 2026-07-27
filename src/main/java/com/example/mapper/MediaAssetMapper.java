package com.example.mapper;

import com.example.config.MapStructConfig;
import com.example.entity.Organization;
import com.example.entity.User;
import com.example.entity.enums.MediaUploadStatus;
import com.example.media.dto.ConfirmMediaUploadResponse;
import com.example.media.dto.InitiateMediaUploadRequest;
import com.example.media.entity.MediaAsset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface MediaAssetMapper {

    @Mapping(target = "mediaAssetId", source = "id")
    ConfirmMediaUploadResponse toConfirmMediaUploadResponse(MediaAsset mediaAsset);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organization", source = "organization")
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
            User user,
            String bucketName,
            String objectKey,
            String mimeType,
            String checksumSha256,
            MediaUploadStatus uploadStatus
    );
}
