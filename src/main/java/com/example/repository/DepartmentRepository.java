package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    @Query("""
            SELECT d
            FROM Department d
            WHERE d.name = :name
            """)
    public Department findByName(String name);

    @Query("""
            SELECT d
            FROM Department d
            WHERE d.id = :id
            """)
    public Department findById(int id);
    @Query("""
            SELECT d
            FROM Department d
            WHERE d.id = :id AND d.organization.id = :orgId
            """)
    public Department findByIdAndOrgId(Integer id, Integer orgId);
}
