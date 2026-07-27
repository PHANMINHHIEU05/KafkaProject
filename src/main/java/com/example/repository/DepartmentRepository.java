package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    @Query("""
            SELECT d
            FROM Department d
            WHERE d.organization.id = :orgId
              AND d.name = :name
            """)
    Department findByNameAndOrgId(@Param("name") String name, @Param("orgId") Integer orgId);

    @Query("""
            SELECT d
            FROM Department d
            WHERE d.id = :id AND d.organization.id = :orgId
            """)
    Department findByIdAndOrgId(@Param("id") Integer id, @Param("orgId") Integer orgId);
}
