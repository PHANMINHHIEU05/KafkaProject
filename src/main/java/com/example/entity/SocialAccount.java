package com.example.entity;

import java.time.Instant;
import java.util.UUID;

import com.example.entity.enums.ConnectionStatus;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(name = "platform" , nullable = false)
    private Platform platform;

    @Column(name = "account_name" , nullable = false)
    private String accountName;
    @Column(name = "external_account_id" , nullable = false)
    private String externalAccountId;
    @Column(name = "active" , nullable = false)
    private boolean active;
    @Enumerated(EnumType.STRING)
    @Column(name = "connect_status" , nullable = false)
    private ConnectionStatus connectStatus;
    @Column(name = "connected_at" , nullable = false)
    private Instant connectedAt;
    @Column(name = "created_at" , nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at" , nullable = false)
    private Instant updatedAt;
}