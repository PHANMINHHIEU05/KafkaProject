package com.example.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.management.relation.Role;

import com.example.entity.enums.UserStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name" , nullable = false)
    private String first_name;

    @Column(name = "last_name" , nullable = false)
    private String last_name;

    @Column(name = "phone_number" , nullable = false , unique = true , length = 10)
    private String phone_number;

    @Column(name = "email" , nullable = false , unique = true , columnDefinition = "CIREXT")
    private String email;

    @Column(name = "avatar_url" )
    private String avatar_url;

    @Column(name = "password" , nullable = false)
    private String password;

    @Column(name = "status" , nullable = false)
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status" , nullable = false)
    private UserStatus user_status;

    @Column(name = "created_at" , nullable = false)
    private Instant created_at;
    @Column(name = "updated_at" , nullable = false)
    private Instant updated_at;
    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<SocialAccount> socialAccounts = new ArrayList<>(); 

    @ManyToMany
    @JoinTable (
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id" , referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id" , referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();

    

    @PrePersist
    void prePersist() {
        this.created_at = Instant.now();
        this.updated_at = Instant.now();
        this.status = "ACTIVE";
    }


    @PreUpdate
    void preUpdate() {
        this.updated_at = Instant.now();
    }
}
