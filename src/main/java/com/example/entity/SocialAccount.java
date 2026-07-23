package com.example.entity;

import java.time.Instant;

import com.example.entity.enums.ConnectionStatus;
import com.example.entity.enums.Platform;

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
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.*;

@Entity
@Table(name = "social_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int  id;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "organization_id" , nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(name = "platform" , nullable = false)
    private Platform platform;

    @Column(name = "account_name" , nullable = false)
    private String accountName;
    @Column(name = "external_account_id" , nullable = false)
    private String externalAccountId;
    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;
    @Column(name = "active" , nullable = false)
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_status" , nullable = false)
    private ConnectionStatus connectionStatus;

    @Column(name = "connected_at" , nullable = false)
    private Instant connectedAt;

    @Column(name = "created_at" , nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at" , nullable = false)
    private Instant updatedAt;

    @Column(name = "access_token_encrypted"  , columnDefinition = "TEXT")
    private String accessTokenEncrypted;
    @Column(name = "refresh_token_encrypted" , columnDefinition = "TEXT")
    private String refreshTokenEncrypted;
    @Column(name = "token_expires_at")
    private Instant tokenExpiresAt;
    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @PrePersist
    protected void prePersist() {
        Instant now = Instant.now();
        this.connectedAt = this.connectedAt == null ? now : this.connectedAt;
        this.createdAt = this.createdAt == null ? now : this.createdAt;
        this.updatedAt = now;
        this.active = true;
        if (this.connectionStatus == null) {
            this.connectionStatus = ConnectionStatus.CONNECTED;
        }
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
