package com.example.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.Post;

public interface PostRepository extends JpaRepository<Post, UUID> {
    @Query("""
        SELECT p 
        FROM Post p
        WHERE p.id = :postId AND p.user.id = :userId
            """)
    Optional<Post> findByIdandUserId(UUID postId, UUID userId);
    
    @Query("""
        SELECT p
        FROM Post p
        WHERE p.clientRequestId = :clientRequestId
            """)
    Optional<Post> findByClientRequestId(UUID clientRequestId);
}
