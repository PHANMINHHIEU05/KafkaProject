package com.example.media.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.media.entity.MediaAsset;
import com.example.media.entity.MediaUploadStatus;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long> {
        @Query("""
                SELECT m 
                FROM MediaAsset m
                WHERE m.id = :id AND m.organization.id = :orgId
                """)
        Optional<MediaAsset> findByIdAndOrgId(@Param("id") Long id, @Param("orgId") Integer orgId);
        @Query("""
                SELECT m 
                FROM MediaAsset m
                WHERE m.id IN :ids AND m.organization.id = :orgId AND m.uploadStatus = :status
                """)        
        List<MediaAsset> findByOrgIdAndStatus(@Param("ids") Collection<Long> ids , @Param("orgId") Integer orgId , @Param("status") MediaUploadStatus status);
        @Query("""
                SELECT m 
                FROM MediaAsset m
                WHERE m.organization.id = :organizationId
                AND m.user.id = :userId
                AND m.uploadStatus <> :excludedStatus      
                        """)
        Page<MediaAsset> findAllByIdUpload(
                Integer organizationId,
                Integer userId,
                MediaUploadStatus excludedStatus,
                Pageable pageable
        );
        @Query("""
                SELECT m
                FROM MediaAsset m
                WHERE m.organization.id = :organizationId
                AND m.department.id = :departmentId
                AND m.uploadStatus = :status    
                        """)
        Page<MediaAsset> findAllDepartmentMedia(
                Integer organizationId,
                Integer departmentId,
                MediaUploadStatus status,
                Pageable pageable
        );
        @Query("""
                SELECT m
                FROM MediaAsset m
                WHERE m.organization.id = :organizationId
                AND m.uploadStatus = :status
                """)
        Page<MediaAsset> findAllOrgMedia(Integer organizationId, MediaUploadStatus status, Pageable pageable);

        @Query("""
                SELECT m
                FROM MediaAsset m
                WHERE m.id = :mediaAssetId
                AND m.organization.id = :organizationId
                AND m.department.id = :departmentId
                AND m.uploadStatus = :uploadStatus
                        """)
        Optional<MediaAsset> findByMediaId(
        @Param("mediaAssetId") Long mediaAssetId,
        @Param("organizationId") Integer organizationId,
        @Param("departmentId") Integer departmentId,
        @Param("uploadStatus") MediaUploadStatus uploadStatus
);
}
