package com.example.media.service;

import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.config.minIO.MinioProperties;
import com.example.entity.OrganizationMember;
import com.example.exception.MediaValidationException;
import com.example.mapper.MediaAssetMapper;
import com.example.media.dto.ConfirmMediaUploadResponse;
import com.example.media.dto.InitiateMediaUploadRequest;
import com.example.media.dto.InitiateMediaUploadResponse;
import com.example.media.dto.MediaAssetResponse;
import com.example.media.dto.MediaDownloadResponse;
import com.example.media.entity.MediaAsset;
import com.example.media.entity.MediaUploadStatus;
import com.example.entity.enums.MediaType;
import com.example.media.repository.MediaAssetRepository;
import com.example.service.AuthorizationService;
import com.example.service.OrganizationMemberService;
import com.example.storage.MinioStorageService;
import com.example.storage.StoredObjectMetadata;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaAssetServiceImpl implements MediaAssetService  {
    private final AuthorizationService authorizationService;
    private static final Set<String> IMAGE_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/gif");
    private static final Set<String> VIDEO_CONTENT_TYPES = Set.of(
            "video/mp4",
            "video/quicktime",
            "video/x-msvideo"
    );
    private final MediaAssetRepository mediaAssetRepository;
    private final OrganizationMemberService organizationMemberService;
    private final MinioStorageService minioStorageService;
    private final MinioProperties minioProperties;
    private final MediaAssetMapper mediaAssetMapper;


    @Override
    @Transactional
    public  InitiateMediaUploadResponse initiateUpload(InitiateMediaUploadRequest request) {
        validateUploadRequest(request);
        OrganizationMember member = organizationMemberService.getCurrentMember();
        Integer organizationId = member.getOrganization().getId();
        Integer userId = member.getUser().getId();
        String objectKey = buildObjectKey(organizationId , userId , request.originalFilename());
        MediaAsset mediaAsset = mediaAssetMapper.toUploadingMediaAsset(
                request,
                member.getOrganization(),
                member.getDepartment(),
                member.getUser(),
                minioProperties.bucket(),
                objectKey,
                normalizeMimeType(request.mimeType())
                ,
                normalizeNullable(request.checksumSha256()),
                MediaUploadStatus.UPLOADING
        );

        MediaAsset savedMediaAsset = mediaAssetRepository.save(mediaAsset);
        String uploadUrl = minioStorageService.createUploadUrl(objectKey);
        
        return new InitiateMediaUploadResponse(
                savedMediaAsset.getId(),
                uploadUrl,
                "PUT",
                java.util.Map.of("Content-Type", savedMediaAsset.getMimeType()),
                Instant.now().plusSeconds(minioProperties.uploadUrlExpirySeconds())
        );
    }

    @Override
    @Transactional
    public ConfirmMediaUploadResponse confirmUpload(Long mediaAssetId) {
        OrganizationMember member = organizationMemberService.getCurrentMember();
        MediaAsset mediaAsset = getMediaAssetOrThrow(mediaAssetId, member.getOrganization().getId());
        if (mediaAsset.getUploadStatus() == MediaUploadStatus.DELETED) {
            throw new MediaValidationException("Media asset đã bị xóa: " + mediaAssetId);
        }
        StoredObjectMetadata metadata = minioStorageService.startObject(mediaAsset.getObjectKey());

        mediaAsset.setEtag(metadata.etag());
        mediaAsset.setObjectVersionId(metadata.versionId());
        mediaAsset.setSizeBytes(metadata.size());
        if (metadata.contentType() != null && !metadata.contentType().isBlank()) {
            mediaAsset.setMimeType(normalizeMimeType(metadata.contentType()));
        }
        mediaAsset.setUploadStatus(MediaUploadStatus.READY);
        mediaAsset.setConfirmedAt(Instant.now());

        return mediaAssetMapper.toConfirmMediaUploadResponse(mediaAssetRepository.save(mediaAsset));
    }

    @Override
    public MediaDownloadResponse createDownloadUrl(Long mediaAssetId) {
        OrganizationMember member = organizationMemberService.getCurrentMember();
        MediaAsset mediaAsset = getMediaAssetOrThrow(mediaAssetId, member.getOrganization().getId());
        if (mediaAsset.getUploadStatus() != MediaUploadStatus.READY) {
            throw new MediaValidationException("Media asset chưa sẵn sàng để tải xuống: " + mediaAssetId);
        }
        String downloadUrl = minioStorageService.createDownloadUrl(mediaAsset.getObjectKey());
        Instant expiresAt = Instant.now().plusSeconds(minioProperties.downloadUrlExpirySeconds());

        return mediaAssetMapper.toDownloadResponse(mediaAsset, downloadUrl, expiresAt);
    }

    @Override
    public java.util.List<MediaAsset> getReadyAssets(
            java.util.Collection<Long> mediaAssetIds,
            Integer organizationId
    ) {
        return mediaAssetRepository.findByOrgIdAndStatus(
                mediaAssetIds,
                organizationId,
                MediaUploadStatus.READY
        );
    }

    @Override
    @Transactional
    public void delete(Long mediaAssetId) {
        OrganizationMember member = organizationMemberService.getCurrentMember();
        MediaAsset mediaAsset = getMediaAssetOrThrow(mediaAssetId, member.getOrganization().getId());
        minioStorageService.deleteObject(mediaAsset.getObjectKey());
        mediaAsset.setUploadStatus(MediaUploadStatus.DELETED);
        mediaAsset.setDeletedAt(Instant.now());
    }
    private void validateUploadRequest (InitiateMediaUploadRequest request){
        String mimeType = request.mimeType().trim().toLowerCase();
        if(request.mediaType() == MediaType.IMAGE){
            if(!IMAGE_CONTENT_TYPES.contains(mimeType)){
                throw new MediaValidationException("Định dạng ảnh không hỗ trợ");
            }
            if(request.sizeBytes() > minioProperties.maxImageSizeBytes()){
                throw new MediaValidationException("Kích thước ảnh vượt quá giới hạn");
            }
        }
        if(request.mediaType() == MediaType.VIDEO){
            if(!VIDEO_CONTENT_TYPES.contains(mimeType)){
                throw new MediaValidationException("Định dạng video không hỗ trợ");
            }
            if(request.sizeBytes() > minioProperties.maxVideoSizeBytes()){
                throw new MediaValidationException("Kích thước video vượt quá giới hạn");
            }
        }
    }
    private String buildObjectKey(Integer organizationId , Integer userId , String originalFilename){
        String extension = extractFileExtension(originalFilename);
        String month = YearMonth.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return "organizations/%d/users/%d/%s/%s%s"
                .formatted(
                        organizationId,
                        userId,
                        month,
                        UUID.randomUUID(),
                        extension
                );
    }
    private String extractFileExtension(String originalFilename){
        int dotIndex = originalFilename.lastIndexOf('.');
        if(dotIndex < 0  || dotIndex == originalFilename.length() - 1){
            throw new MediaValidationException("Tên tệp không hợp lệ hoặc không có phần mở rộng");
        }
        String extension = originalFilename.substring(dotIndex + 1).toLowerCase();
        if (!extension.matches("[a-z0-9]{1,10}")) {
            return "";
        }
        return "." + extension;
    }
        private String normalizeMimeType(
            String mimeType
    ) {
        return mimeType
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private MediaAsset getMediaAssetOrThrow(Long mediaAssetId, Integer organizationId) {
        return mediaAssetRepository.findByIdAndOrgId(mediaAssetId, organizationId)
                .orElseThrow(() -> new MediaValidationException("Không tìm thấy media asset: " + mediaAssetId));
    }

    @Transactional 
    @Override
    public Page<MediaAssetResponse> getMyMedia(Pageable pageable) {
        authorizationService.requirePermission("MEDIA_READ");
        OrganizationMember member = organizationMemberService.getCurrentMember();
        Page<MediaAsset> mediaAssets = mediaAssetRepository.findAllByIdUpload(
            member.getOrganization().getId(),
            member.getUser().getId(),
            MediaUploadStatus.DELETED,
            pageable
        );
        return mediaAssets.map(mediaAssetMapper::toResponse);
    }
    @Override
    @Transactional()
    public Page<MediaAssetResponse> getVisibleMedia(
            Pageable pageable
    ) {
        authorizationService.requirePermission(
                "MEDIA_READ"
        );

        OrganizationMember member =
                organizationMemberService.getCurrentMember();

        Integer organizationId =
                member.getOrganization().getId();

        Integer departmentId =
                member.getDepartment().getId();

        Page<MediaAsset> mediaAssets;

        if (authorizationService.hasPermission(
                "MEDIA_READ_ORGANIZATION"
        )) {
            // Có quyền toàn tổ chức
            mediaAssets = mediaAssetRepository
                    .findAllOrgMedia(
                            organizationId,
                            MediaUploadStatus.READY,
                            pageable
                    );
        } else {
            // Chỉ được xem trong phòng ban
            mediaAssets = mediaAssetRepository
                    .findAllDepartmentMedia(
                            organizationId,
                            departmentId,
                            MediaUploadStatus.READY,
                            pageable
                    );
        }

        return mediaAssets.map(mediaAssetMapper::toResponse);
    }
    @Override
    @Transactional
    public MediaAssetResponse getMediaAssetById(Long mediaAssetId) {
        authorizationService.requirePermission("MEDIA_READ");
        OrganizationMember member = organizationMemberService.getCurrentMember();
        MediaAsset mediaAsset = mediaAssetRepository.findByMediaId(
                        mediaAssetId,
                        member.getOrganization().getId(),
                        member.getDepartment().getId(),
                        MediaUploadStatus.READY
                )
                .orElseThrow(() -> new MediaValidationException("Không tìm thấy media asset: " + mediaAssetId));
        if (mediaAsset.getUploadStatus() != MediaUploadStatus.READY) {
            throw new MediaValidationException("Media asset chưa sẵn sàng để xem: " + mediaAssetId);
        }
        if (!authorizationService.hasPermission("MEDIA_READ_ORGANIZATION")
                && !mediaAsset.getDepartment().getId().equals(member.getDepartment().getId())) {
            throw new MediaValidationException("Bạn không có quyền xem media asset này: " + mediaAssetId);      
        }
        return mediaAssetMapper.toResponse(mediaAsset);
    }
    
}
