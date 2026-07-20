package com.example.entity;

import java.time.Instant;
import java.util.UUID;

import com.example.entity.enums.OutboxStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutBox {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "aggregate_id" , nullable = false)
    private UUID aggregateId;

    @Column(name = "event_type" , nullable = false)
    private String eventType;

    @Column(name = "topic" , nullable = false)
    private String topic;
    @Column(name = "event_key" , nullable = false)
    private String eventKey;

    @Column(name = "playload" , columnDefinition = "JSONB" , nullable = false)
    private String playload;

    @Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(name = "status" , nullable = false)
    private OutboxStatus status;
    @Column(name = "retry_count" , nullable = false)
    private Integer retryCount;
    @Column(name = "max_retry" , nullable = false)
    private Integer maxRetry;

    @Column(name = "available_at" , nullable = false)
    private Instant availableAt;
    @Column(name = "published_at" , nullable = false)
    private Instant publishedAt;
    @Column(name = "error_message" , columnDefinition = "TEXT")
    private String errorMessage;
    @Column(name = "error_code" , nullable = false)
    private String errorCode;
    @Column(name = "created_at" , nullable = false)
    private Instant createdAt;
}
