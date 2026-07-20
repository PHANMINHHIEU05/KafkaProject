package com.example.entity;

import java.time.Instant;
import java.util.UUID;

import com.example.entity.enums.PublishStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_target")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostTarget {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id" , nullable = false)
    private Post post;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "social_account_id" , nullable = false)
    private SocialAccount socialAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform" , nullable = false)
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
}   

