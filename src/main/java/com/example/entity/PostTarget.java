package com.example.entity;

import java.time.Instant;

import com.example.entity.enums.Platform;
import com.example.entity.enums.PublishStatus;

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
import jakarta.persistence.Version;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_target")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostTarget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id" , nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "social_channel_id" , nullable = false)
    private SocialChannel socialChannel;

    @Transient
    private SocialAccount socialAccount;

    @Transient
    private Platform platform;

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false)
    private PublishStatus status;

    @Column(name = "idempotency_key" , nullable = false)
    private String idempotencyKey;

    @Column(name = "external_post_id" )
    private String externalPostId;

    @Column(name = "external_post_url" , columnDefinition = "TEXT" )
    private String externalPostUrl;

    @Column(name = "error_message" , columnDefinition = "TEXT")
    private String errorMessage;
    @Column(name = "error_code")
    private String errorCode;
    @Column(name = "processing_started_at")
    private Instant processingStartedAt;
    @Column(name = "published_at")
    private Instant publishedAt;
    @Version
    @Column(name = "version" , nullable = false)
    private Long version;
    @Column(name = "created_at" , nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at" , nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void prePersist() {
        Instant now = Instant.now();
        this.createdAt = this.createdAt == null ? now : this.createdAt;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = PublishStatus.PENDING;
        }
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = Instant.now();
    }

    @Transient
    public SocialAccount getSocialAccount() {
        if (socialChannel != null) {
            return socialChannel.getSocialAccount();
        }
        return socialAccount;
    }

    @Transient
    public Platform getPlatform() {
        if (socialChannel != null && socialChannel.getSocialAccount() != null) {
            return socialChannel.getSocialAccount().getPlatform();
        }
        return platform;
    }
}   
