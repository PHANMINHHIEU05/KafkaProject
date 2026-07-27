package com.example.repository;

import com.example.entity.PostTarget;
import com.example.entity.enums.Platform;
import com.example.entity.enums.PublishStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostTargetRepository extends JpaRepository<PostTarget, Long> {

    @Query(
        value = """
            SELECT *
            FROM post_target
            WHERE post_id = :postId
            ORDER BY created_at ASC
            """,
        nativeQuery = true
    )
    List<PostTarget> findAllByPostId(@Param("postId") Long postId);

    @Query(
        value = """
            SELECT *
            FROM post_target
            WHERE status = CAST(:status AS varchar)
              AND social_channel_id IN (
                  SELECT sc.id
                  FROM social_channel sc
                  JOIN social_account sa
                    ON sa.id = sc.social_account_id
                  WHERE sa.platform = CAST(:platform AS varchar)
                    AND sa.active = true
                    AND sc.can_publish = true
                    AND sc.status = 'ACTIVE'
              )
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
