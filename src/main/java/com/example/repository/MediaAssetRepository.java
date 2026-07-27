package com.example.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.media.entity.MediaAsset;
import com.example.media.entity.MediaUploadStatus;

public interface MediaAssetRepository  extends JpaRepository<MediaAsset, Long> {
    @Query("""
            SELECT ma
            FROM MediaAsset ma
            WHERE ma.id IN :ids
              AND ma.organization.id = :orgId
              AND ma.uploadStatus = :status
            """)
    List<MediaAsset> findAllReadyInOrg(@Param("ids") Collection<Long> ids, @Param("orgId") Integer orgId, @Param("status") MediaUploadStatus status);
}
