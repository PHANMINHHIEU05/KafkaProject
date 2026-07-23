package com.example.repository;

import com.example.entity.OutBox;
import com.example.entity.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutBox, Long> {
    @Query(
        value = """
            SELECT *
            FROM outbox_event
            WHERE status IN ('NEW', 'RETRY_WAIT')
              AND available_at <= CURRENT_TIMESTAMP
            ORDER BY available_at ASC, created_at ASC
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """,
        nativeQuery = true
    )
    List<OutBox> findReadyEvents(
        @Param("limit") int limit
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE OutBox o
        SET o.status = :processingStatus
        WHERE o.id = :id
          AND o.status IN (:newStatus, :retryStatus)
        """)
    int markProcessing(
        @Param("id") Long id,
        @Param("newStatus") OutboxStatus newStatus,
        @Param("retryStatus") OutboxStatus retryStatus,
        @Param("processingStatus") OutboxStatus processingStatus
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE OutBox o
        SET o.status = :publishedStatus,
            o.publishedAt = :publishedAt,
            o.errorCode = NULL,
            o.errorMessage = NULL
        WHERE o.id = :id
        """)
    int markPublished(
        @Param("id") Long id,
        @Param("publishedStatus") OutboxStatus publishedStatus,
        @Param("publishedAt") Instant publishedAt
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE OutBox o
        SET o.status = :retryStatus,
            o.retryCount = :retryCount,
            o.availableAt = :availableAt,
            o.errorCode = :errorCode,
            o.errorMessage = :errorMessage
        WHERE o.id = :id
        """)
    int markRetry(
        @Param("id") Long id,
        @Param("retryStatus") OutboxStatus retryStatus,
        @Param("retryCount") Integer retryCount,
        @Param("availableAt") Instant availableAt,
        @Param("errorCode") String errorCode,
        @Param("errorMessage") String errorMessage
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE OutBox o
        SET o.status = :deadStatus,
            o.retryCount = :retryCount,
            o.errorCode = :errorCode,
            o.errorMessage = :errorMessage
        WHERE o.id = :id
        """)
    int markDead(
        @Param("id") Long id,
        @Param("deadStatus") OutboxStatus deadStatus,
        @Param("retryCount") Integer retryCount,
        @Param("errorCode") String errorCode,
        @Param("errorMessage") String errorMessage
    );
}