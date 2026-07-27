package com.example.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.OrganizationMember;
import com.example.entity.enums.OrganizationMemStatus;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Long> {
    @Query("""
            SELECT om
            FROM OrganizationMember om
            WHERE om.user.id = :userId
            """)
    Optional<OrganizationMember> findByUserId(@Param("userId") Integer id);

    @Query("""
            SELECT om
            FROM OrganizationMember om
            WHERE om.user.id = :userId AND om.status = :status
            """)
    Optional<OrganizationMember> findByUserIdAndStatus(@Param("userId") int userId, @Param("status") OrganizationMemStatus status);

    @Query("""
            SELECT DISTINCT om
            FROM OrganizationMember om
            JOIN FETCH om.user u
            LEFT JOIN FETCH om.roles r
            LEFT JOIN FETCH r.permissions
            WHERE u.email = :email
              AND om.status = :status
            """)
    Optional<OrganizationMember> findActiveByUserEmailWithRolesAndPermissions(
            @Param("email") String email,
            @Param("status") OrganizationMemStatus status
    );
    
    @Query("""
            SELECT om
            FROM OrganizationMember om
            WHERE om.organization.id = :organizationId
            """)
    List<OrganizationMember> findByOrganizationId(@Param("organizationId") int organizationId);

    @Query("""
            SELECT COUNT(om) > 0
            FROM OrganizationMember om
            WHERE om.user.id = :userId
            """)
    boolean existsByUser(@Param("userId") int userId);

    @Query("""
            SELECT om
            FROM OrganizationMember om
            WHERE om.organization.id = :organizationId
              AND om.department.id = :departmentId
              AND om.status = :status
            """)
    List<OrganizationMember> findByDepartmentInOrgAndStatus(
            @Param("organizationId") Integer organizationId,
            @Param("departmentId") Integer departmentId,
            @Param("status") OrganizationMemStatus status
    );
}
