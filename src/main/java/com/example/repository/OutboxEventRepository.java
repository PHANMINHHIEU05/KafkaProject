package com.example.repository;

import com.example.entity.OutBox;
import com.example.entity.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutBox, UUID> {

    @Query(
        value = """
            SELECT *
            FROM outbox_event
            WHERE status IN ('NEW', 'RETRY_WAIT')
              AND available_at <= CURRENT_TIMESTAMP
            ORDER BY created_at ASC
            LIMIT :limit
            """,
        nativeQuery = true
    )
    List<OutBox> findReadyEvents(@Param("limit") int limit);

    @Modifying
    @Query(
        value = """
            UPDATE outbox_event
            SET status = CAST(:status AS varchar),
                published_at = :publishedAt,
                error_code = NULL,
                error_message = NULL
            WHERE id = :id
            """,
        nativeQuery = true
    )
    int updatePublished(
        @Param("id") UUID id,
        @Param("status") String status,
        @Param("publishedAt") Instant publishedAt
    );

    default int markPublished(UUID id) {
        return updatePublished(id, OutboxStatus.PUBLISHED.name(), Instant.now());
    }
}
