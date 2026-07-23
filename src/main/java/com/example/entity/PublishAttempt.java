package com.example.entity;

import com.example.entity.enums.AttemptStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
    name = "publish_attempt",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_publish_attempt_target_number",
            columnNames = {
                "post_target_id",
                "attempt_number"
            }
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublishAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "post_target_id",
        nullable = false
    )
    private PostTarget postTarget;

    @Column(
        name = "attempt_number",
        nullable = false
    )
    private Integer attemptNumber;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 20
    )
    private AttemptStatus status;

    @Column(
        name = "request_id",
        nullable = false,
        length = 255
    )
    private String requestId;

    @Column(name = "provider_request_id")
    private String providerRequestId;

    @Column(name = "http_status_code")
    private Integer httpStatusCode;

    @Column(
        name = "error_code",
        length = 100
    )
    private String errorCode;

    @Column(
        name = "error_message",
        columnDefinition = "TEXT"
    )
    private String errorMessage;

    @Column(
        name = "retryable",
        nullable = false
    )
    private Boolean retryable;

    @Column(
        name = "started_at",
        nullable = false
    )
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @PrePersist
    protected void prePersist() {
        if (status == null) {
            status = AttemptStatus.PROCESSING;
        }

        if (retryable == null) {
            retryable = false;
        }

        if (startedAt == null) {
            startedAt = Instant.now();
        }

        if (requestId == null || requestId.isBlank()) {
            requestId = java.util.UUID.randomUUID().toString();
        }
    }
}
