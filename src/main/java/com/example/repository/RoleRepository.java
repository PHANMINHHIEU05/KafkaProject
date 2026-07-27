package com.example.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>  {
    @Query("""
            SELECT r
            From Role r
            WHERE r.id IN :ids
              AND r.organization.id = :orgId
              AND r.department.id = :departmentId
              AND r.active = true
            """)
    Set<Role> findActiveByIdsInScope(
            @Param("ids") Set<Integer> ids,
            @Param("orgId") Integer orgId,
            @Param("departmentId") Integer departmentId
    );
}
