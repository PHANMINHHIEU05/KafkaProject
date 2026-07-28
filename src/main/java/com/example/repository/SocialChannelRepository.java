package com.example.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.SocialChannel;
import com.example.entity.enums.ConnectionStatus;
import com.example.entity.enums.SocialChannelStatus;

public interface SocialChannelRepository extends JpaRepository<SocialChannel, Integer> {
    @Query("""
            SELECT sc
            FROM SocialChannel sc
            JOIN FETCH sc.socialAccount sa
            WHERE sc.id IN :ids
              AND sa.organization.id = :orgId
              AND sc.canPublish = true
              AND sc.status = :status
              AND sa.connectionStatus = :connectionStatus
              AND sa.active = true
            """)
    List<SocialChannel> findPublishChannels(
    @Param("ids") Collection<Integer> ids, 
    @Param("orgId") Integer orgId, 
    @Param("status") SocialChannelStatus status, 
    @Param("connectionStatus") ConnectionStatus connectionStatus
    );

    @Query("""
            SELECT sc
            FROM SocialChannel sc
            JOIN FETCH sc.socialAccount sa
            JOIN FETCH sa.user u
            WHERE sa.organization.id = :orgId
            ORDER BY sa.platform ASC, sc.channelName ASC
            """)
    List<SocialChannel> findByOrganizationIdWithAccount(@Param("orgId") Integer orgId);
}
