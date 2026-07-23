package com.example.repository;

import com.example.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {

    @Query(
        value = """
            SELECT *
            FROM post_media
            WHERE post_id = :postId
            ORDER BY sort_order ASC
            """,
        nativeQuery = true
    )
    List<PostMedia> findAllByPostId(@Param("postId") Long postId);

    
}
