package com.example.repository;

import com.example.entity.Post;
import com.example.entity.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("""
        SELECT p
        FROM Post p
        WHERE p.id = :postId
          AND p.user.id = :userId
    """)
    Optional<Post> findByIdAndUserId(
        @Param("postId") UUID postId,
        @Param("userId") UUID userId
    );
    @Query("""
        SELECT p
        FROM Post p
        WHERE p.user.id = :userId
          AND p.clientRequestId = :clientRequestId
    """)
    Optional<Post> findByUserIdAndClientRequestId(
        @Param("userId") UUID userId,
        @Param("clientRequestId") String clientRequestId
    );

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM Post p
        WHERE p.user.id = :userId
          AND p.clientRequestId = :clientRequestId
    """)
    boolean existsByUserIdAndClientRequestId(
        @Param("userId") UUID userId,
        @Param("clientRequestId") String clientRequestId
    );

    @Query("""
        SELECT p
        FROM Post p
        WHERE p.user.id = :userId
          AND (:status IS NULL OR p.status = :status)
        ORDER BY p.createdAt DESC
    """)
    Page<Post> findAllByUserIdAndStatus(
        @Param("userId") UUID userId,
        @Param("status") PostStatus status,
        Pageable pageable
    );

    @Query("""
        SELECT p
        FROM Post p
        WHERE p.status = 'SCHEDULED'
          AND p.scheduledAt <= :now
        ORDER BY p.scheduledAt ASC
    """)
    Page<Post> findScheduledPostsReady(
        @Param("now") Instant now,
        Pageable pageable
    );

    @Modifying(
        flushAutomatically = true,
        clearAutomatically = true
    )
    @Query(
        value = """
            UPDATE post
            SET status = :newStatus,
                updated_at = :updatedAt,
                version = version + 1
            WHERE id = :postId
              AND status = :expectedStatus
            """,
        nativeQuery = true
    )
    int updateStatusIfCurrentStatus(
        @Param("postId") UUID postId,
        @Param("expectedStatus") String expectedStatus,
        @Param("newStatus") String newStatus,
        @Param("updatedAt") Instant updatedAt
    );

    @Modifying(
        flushAutomatically = true,
        clearAutomatically = true
    )
    @Query(
        value = """
            UPDATE post
            SET status = 'CANCELLED',
                updated_at = :updatedAt,
                version = version + 1
            WHERE id = :postId
              AND user_id = :userId
              AND status IN (
                  'DRAFT',
                  'SCHEDULED',
                  'QUEUED',
                  'PROCESSING'
              )
            """,
        nativeQuery = true
    )
    int cancelPost(
        @Param("postId") UUID postId,
        @Param("userId") UUID userId,
        @Param("updatedAt") Instant updatedAt
    );
}