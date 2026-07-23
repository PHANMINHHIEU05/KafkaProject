package com.example.entity;


import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.example.entity.enums.OrganizationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organization")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "slug" , nullable = false , unique = true)
    private String slug;
    @Column(name = "description")
    private String description;
    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;
    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false, length = 20)
    private OrganizationStatus status;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Builder.Default
    @OneToMany(mappedBy = "organization")
    private Set<Department> departments = new HashSet<>();
    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.status == null) {
            this.status = OrganizationStatus.ACTIVE;
        }
    }
    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }
    @Builder.Default
    @OneToMany(mappedBy = "organization")
    private Set<OrganizationMember> members = new HashSet<>();
    @Builder.Default
    @OneToMany(mappedBy = "organization")
    private Set<DepartmentMember> departmentMembers = new HashSet<>();
}
