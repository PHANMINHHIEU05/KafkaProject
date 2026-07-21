package com.example.entity;

import java.time.Instant;
import java.util.UUID;

import com.example.entity.enums.OutboxStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "outbox_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutBox {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "aggregate_id" , nullable = false)
    private UUID aggregateId;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "event_type" , nullable = false)
    private String eventType;

    @Column(name = "topic" , nullable = false)
    private String topic;
    @Column(name = "event_key" , nullable = false)
    private String eventKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload" , columnDefinition = "JSONB" , nullable = false)
    private JsonNode payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false)
    private OutboxStatus status;
    @Column(name = "retry_count" , nullable = false)
    private Integer retryCount;
    @Column(name = "max_retry" , nullable = false)
    private Integer maxRetry;

    @Column(name = "available_at" , nullable = false)
    private Instant availableAt;
    @Column(name = "published_at")
    private Instant publishedAt;
    @Column(name = "error_message" , columnDefinition = "TEXT")
    private String errorMessage;
    @Column(name = "error_code")
    private String errorCode;
    @Column(name = "created_at" , nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void prePersist() {
        Instant now = Instant.now();
        this.createdAt = this.createdAt == null ? now : this.createdAt;
        this.availableAt = this.availableAt == null ? now : this.availableAt;
        this.retryCount = this.retryCount == null ? 0 : this.retryCount;
        this.maxRetry = this.maxRetry == null ? 10 : this.maxRetry;
        if (this.status == null) {
            this.status = OutboxStatus.NEW;
        }
    }
}