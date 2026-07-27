package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.SocialAccount;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Integer> {
    @Query("""
            SELECT sa
            FROM SocialAccount sa
            WHERE sa.id = :id
              AND sa.organization.id = :orgId
            """)
    Optional<SocialAccount> findByIdAndOrgId(@Param("id") Integer id, @Param("orgId") Integer orgId);

    @Query("""
            SELECT sa
            FROM SocialAccount sa
            WHERE sa.organization.id = :orgId
              AND sa.user.id = :userId
              AND sa.active = true
            ORDER BY sa.createdAt DESC
            """)
    List<SocialAccount> findActiveAccountsByOrgIdAndUserId(
            @Param("orgId") Integer orgId,
            @Param("userId") Integer userId
    );

    @Query("""
            SELECT COUNT(sa) > 0
            FROM SocialAccount sa
            WHERE sa.organization.id = :orgId
              AND sa.platform = :platform
              AND sa.externalAccountId = :externalAccountId
            """)
    boolean existsByOrgIdAndPlatformAndExternalAccountId(
            @Param("orgId") Integer orgId,
            @Param("platform") com.example.entity.enums.Platform platform,
            @Param("externalAccountId") String externalAccountId
    );
}
