package com.example.repository;

import com.example.entity.PublishAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PublishAttemptRepository extends JpaRepository<PublishAttempt, Long> {

    @Query(
        value = """
            SELECT *
            FROM publish_attempt
            WHERE post_target_id = :postTargetId
            ORDER BY attempt_number ASC
            """,
        nativeQuery = true
    )
    List<PublishAttempt> findAllByPostTargetId(@Param("postTargetId") UUID postTargetId);

    @Query(
        value = """
            SELECT COALESCE(MAX(attempt_number), 0) + 1
            FROM publish_attempt
            WHERE post_target_id = :postTargetId
            """,
        nativeQuery = true
    )
    int nextAttemptNumber(@Param("postTargetId") UUID postTargetId);
    @Query("""
        SELECT COALESCE(MAX(a.attemptNumber), 0)
        FROM PublishAttempt a
        WHERE a.postTarget.id = :postTargetId
        """)
    int findMaxAttemptNumber(
        @Param("postTargetId") UUID postTargetId
    );
    @Query("""
        SELECT a
        FROM PublishAttempt a
        WHERE a.id = :attemptId
        """)
    Optional<PublishAttempt> findById(
        @Param("attemptId") Long attemptId
    );
}
