package com.example.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.enums.MediaUploadStatus;
import com.example.media.entity.MediaAsset;

public interface MediaAssetRepository  extends JpaRepository<MediaAsset, Long> {
    @Query("""
            SELECT ma
            FROM MediaAsset ma
            Where ma.id in :ids and ma.organization.id = :orgId and ma.status = :status
            """)
    Optional<MediaAsset> findAll(@Param("ids") Collection<Long> ids, @Param("orgId") Integer orgId, @Param("status") MediaUploadStatus status);
}
