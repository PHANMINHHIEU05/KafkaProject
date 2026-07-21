package com.example.repository;

import com.example.entity.PostTarget;
import com.example.entity.enums.Platform;
import com.example.entity.enums.PublishStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostTargetRepository extends JpaRepository<PostTarget, UUID> {

    @Query(
        value = """
            SELECT *
            FROM post_target
            WHERE post_id = :postId
            ORDER BY created_at ASC
            """,
        nativeQuery = true
    )
    List<PostTarget> findAllByPostId(@Param("postId") UUID postId);

    @Query(
        value = """
            SELECT *
            FROM post_target
            WHERE platform = CAST(:platform AS varchar)
              AND status = CAST(:status AS varchar)
            ORDER BY created_at ASC
            LIMIT :limit
            """,
        nativeQuery = true
    )
    List<PostTarget> findReadyTargets(
        @Param("platform") String platform,
        @Param("status") String status,
        @Param("limit") int limit
    );

    default List<PostTarget> findReadyTargets(
        Platform platform,
        PublishStatus status,
        int limit
    ) {
        return findReadyTargets(platform.name(), status.name(), limit);
    }

    @Query(
        value = """
            SELECT *
            FROM post_target
            WHERE idempotency_key = :idempotencyKey
            """,
        nativeQuery = true
    )
    Optional<PostTarget> findByIdempotencyKey(@Param("idempotencyKey") String idempotencyKey);
}
