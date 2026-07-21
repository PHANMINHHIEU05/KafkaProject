package com.example.entity;

import java.time.Instant;

import com.example.entity.enums.AttemptStatus;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "publish_attempt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublishAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "post_target_id", nullable = false)
    private PostTarget postTarget;

    @Column(name = "attempt_number", nullable = false)
    private int attemptNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttemptStatus status;

    @Column(name = "request_id" , nullable = false)
    private String requestId;

    @Column(name = "http_status_code")
    private Integer httpStatusCode;

    @Column(name = "retryable" , nullable = false)
    private boolean retryable;
    @Column(name = "error_code")
    private String errorCode;
    @Column(name = "error_message" , columnDefinition = "TEXT")
    private String errorMessage;
    @Column(name = "started_at" , nullable = false)
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @PrePersist
    protected void prePersist() {
        this.startedAt = this.startedAt == null ? Instant.now() : this.startedAt;
        if (this.status == null) {
            this.status = AttemptStatus.PROCESSING;
        }
    }
    
}
