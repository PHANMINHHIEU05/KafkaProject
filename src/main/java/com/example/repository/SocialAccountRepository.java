package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.SocialAccount;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Integer> {
    @Query(
        value = """
            SELECT *
            FROM social_account
            WHERE user_id = :userId
              AND id IN (:accountIds)
              AND active = true
            """,
        nativeQuery = true
    )
    List<SocialAccount> findActiveAccountsByIds(@Param("userId") Integer userId, @Param("accountIds") List<Integer> accountIds);

    @Query(
        value = """
            SELECT *
            FROM social_account
            WHERE user_id = :userId
              AND active = true
            ORDER BY created_at DESC
            """,
        nativeQuery = true
    )
    List<SocialAccount> findActiveAccountsByUserId(@Param("userId") Integer userId);

    @Query(
        value = """
            SELECT EXISTS (
                SELECT 1
                FROM social_account
                WHERE platform = CAST(:platform AS varchar)
                  AND external_account_id = :externalAccountId
            )
            """,
        nativeQuery = true
    )
    boolean existsByPlatformAndExternalAccountId(
        @Param("platform") String platform,
        @Param("externalAccountId") String externalAccountId
    );
}
