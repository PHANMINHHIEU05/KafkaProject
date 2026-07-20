package com.example.entity;

import java.time.Instant;
import java.util.UUID;

import com.example.entity.enums.PostStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;
    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "scheduled_time")
    private Instant scheduled_time;
    @Version
    @Column(name = "version")
    private Integer version;
    @Column(name = "created_at" , nullable = false)
    private Instant created_at;
    @Column(name = "updated_at" , nullable = false)
    private Instant updated_at;

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false)
    private PostStatus postStatus;

    @PrePersist
    void prePersist() {
        this.created_at = Instant.now();
        this.updated_at = Instant.now();
        if (this.postStatus == null) {
            this.postStatus = PostStatus.DRAFT;
        }
    }
    @PreUpdate
    void preUpdate() {
        this.updated_at = Instant.now();
    }

}
