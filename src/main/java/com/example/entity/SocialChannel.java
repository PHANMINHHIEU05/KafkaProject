package com.example.entity;

import java.time.Instant;


import com.example.entity.enums.SocialChannelStatus;
import com.example.entity.enums.SocialChannelType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "social_channel")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_account_id", nullable = false)
    private SocialAccount socialAccount;

    @Column(name = "external_channel_id", nullable = false)
    private String externalChannelId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false)
    private SocialChannelType channelType;

    @Column(name = "channel_name", nullable = false)
    private String channelName;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "can_publish", nullable = false)
    private boolean canPublish;
    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false)
    private SocialChannelStatus status;

    @Column(name = "channel_access_token_encrypted" , columnDefinition = "TEXT")
    private String channelAccessToken;
    @Column(name = "channel_token_expires_at")
    private Instant channelTokenExpiresAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata" , columnDefinition = "JSONB")
    private JsonNode metadata;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        this.createdAt = this.createdAt == null ? now : this.createdAt;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = SocialChannelStatus.ACTIVE;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
