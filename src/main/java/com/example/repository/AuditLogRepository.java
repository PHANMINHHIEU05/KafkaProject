package com.example.repository;

import com.example.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query(
        value = """
            SELECT *
            FROM audit_log
            WHERE actor_user_id = :actorUserId
            ORDER BY created_at DESC
            """,
        nativeQuery = true
    )
    List<AuditLog> findAllByActorUserId(@Param("actorUserId") Integer actorUserId);

    @Query(
        value = """
            SELECT *
            FROM audit_log
            WHERE target_type = :targetType AND target_id = :targetId
            ORDER BY created_at DESC
            """,
        nativeQuery = true
    )
    List<AuditLog> findAllByTarget(
        @Param("targetType") String targetType,
        @Param("targetId") Long targetId
    );
}
