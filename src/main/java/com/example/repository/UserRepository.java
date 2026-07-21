package com.example.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.User;

public interface UserRepository  extends JpaRepository<User, UUID> {
    @Query(
        value = """
            SELECT *
            FROM users
            WHERE id = :userId
            """,
        nativeQuery = true
    )
    Optional<User> findById(@Param("userId") UUID userId);

    @Query(
        value = """
            SELECT *
            FROM users
            WHERE email = :email
            """,
        nativeQuery = true
    )
    Optional<User> findByEmail(@Param("email") String email);
}
