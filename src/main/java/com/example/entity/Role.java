package com.example.entity;

import java.util.HashSet;
import java.util.Set;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name" , nullable = false, length = 100)
    private String name;
    @Column(name = "description" )
    private String description;
    @Column(name = "active" , nullable = false)
    private Boolean active;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permission",
        joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "organization_member_role",
        joinColumns = @JoinColumn(name = "role_id" , referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "organization_member_id" , referencedColumnName = "id")
    )
    @Builder.Default
    private Set<OrganizationMember> organizationMembers = new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        this.createdAt = this.createdAt == null ? now : this.createdAt;
        this.updatedAt = now;
        if (this.active == null) {
            this.active = true;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
