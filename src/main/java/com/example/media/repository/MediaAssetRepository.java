package com.example.media.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.enums.MediaUploadStatus;
import com.example.media.entity.MediaAsset;

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
    
}
