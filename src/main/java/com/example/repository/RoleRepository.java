package com.example.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>  {
    @Query("""
            SELECT r
            From Role r
            WHERE r.id IN :ids
            """)
    Set<Role> findByIds(Set<Integer> ids);
}
