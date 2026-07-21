package com.example.entity;

import com.example.entity.enums.PostStatus;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "user_id",
        nullable = false
    )
    private User user;

    @Column(
        name = "title",
        length = 500
    )
    private String title;

    @Column(
        name = "content",
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    private PostStatus status;

    @Column(
        name = "client_request_id",
        length = 255
    )
    private String clientRequestId;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Version
    @Column(
        name = "version",
        nullable = false
    )
    private Long version;

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    private Instant createdAt;

    @Column(
        name = "updated_at",
        nullable = false
    )
    private Instant updatedAt;

    @OneToMany(
        mappedBy = "post",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<PostMedia> media = new ArrayList<>();

    @OneToMany(
        mappedBy = "post",
        cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
        }
    )
    @Builder.Default
    private List<PostTarget> targets = new ArrayList<>();

    @PrePersist
    protected void prePersist() {
        Instant now = Instant.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;

        if (status == null) {
            status = PostStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void preUpdate() {
        updatedAt = Instant.now();
    }

    public void addMedia(PostMedia postMedia) {
        if (postMedia == null) {
            return;
        }

        postMedia.setPost(this);
        media.add(postMedia);
    }

    public void removeMedia(PostMedia postMedia) {
        if (postMedia == null) {
            return;
        }

        media.remove(postMedia);
        postMedia.setPost(null);
    }

    public void addTarget(PostTarget postTarget) {
        if (postTarget == null) {
            return;
        }

        postTarget.setPost(this);
        targets.add(postTarget);
    }

    public void removeTarget(PostTarget postTarget) {
        if (postTarget == null) {
            return;
        }

        targets.remove(postTarget);
        postTarget.setPost(null);
    }
}