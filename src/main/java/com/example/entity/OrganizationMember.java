package com.example.entity;

import java.util.HashSet;
import java.util.Set;

import com.example.entity.enums.OrganizationMemStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organization_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationMember {
    @Id 
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY )
    private long id;
    @OneToOne(fetch = jakarta.persistence.FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrganizationMemStatus status;

    @Column(name = "joined_at")
    private java.time.Instant joinedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.Instant createdAt;    
    @Column(name = "updated_at", nullable = false)
    private java.time.Instant updatedAt;
    @PrePersist
    void prePersist() {
        java.time.Instant now = java.time.Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = OrganizationMemStatus.ACTIVE;
        }
    }
    @PreUpdate
    void preUpdate() {
        this.updatedAt = java.time.Instant.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @Builder.Default
    @OneToMany(mappedBy = "organizationMember", fetch = FetchType.LAZY)
    private Set<DepartmentMember> departmentMembers = new HashSet<>();
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY , mappedBy = "organizationMembers")
    private Set<Role> roles = new HashSet<>();

}
