package com.example.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.SocialChannel;
import com.example.entity.enums.ConnectionStatus;
import com.example.entity.enums.SocialChannelStatus;

public interface SocialChannelRepository extends JpaRepository<SocialChannel, Integer> {
    @Query("""
            SELECT 
            FROM SocialChannel sc
            JOIN FETCH sc.SocialAccount sa 
            WHERE sc.id in :ids and
            sa.organization.id = :orgId and sc.status = :status and sa.connectionStatus = :connectionStatus
            """)
    Optional<SocialChannel> findPublishChannels(
    @Param("ids") Collection<Integer> ids, 
    @Param("orgId") Integer orgId, 
    @Param("status") SocialChannelStatus status, 
    @Param("connectionStatus") ConnectionStatus connectionStatus
    );
}
