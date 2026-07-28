package com.example.service;

import com.example.dto.DepartmentResponse;
import com.example.dto.OrganizationMemberSummaryResponse;
import com.example.dto.OrganizationResponse;
import com.example.dto.PermissionCheckResponse;
import com.example.dto.RoleSummaryResponse;
import com.example.dto.SocialChannelResponse;
import com.example.entity.Department;
import com.example.entity.Organization;
import com.example.entity.OrganizationMember;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.entity.SocialChannel;
import com.example.repository.DepartmentRepository;
import com.example.repository.OrganizationMemberRepository;
import com.example.repository.RoleRepository;
import com.example.repository.SocialChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoPermissionViewService {
    private final AuthorizationService authorizationService;
    private final OrganizationMemberService organizationMemberService;
    private final DepartmentRepository departmentRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final RoleRepository roleRepository;
    private final SocialChannelRepository socialChannelRepository;

    @Transactional(readOnly = true)
    public PermissionCheckResponse checkCurrentPermission(String permissionCode) {
        return new PermissionCheckResponse(
            permissionCode,
            authorizationService.hasPermission(permissionCode)
        );
    }

    @Transactional(readOnly = true)
    public OrganizationResponse getCurrentOrganization() {
        authorizationService.requirePermission("ORGANIZATION_READ");
        Organization organization = organizationMemberService.getCurrentOrganization();

        return new OrganizationResponse(
            organization.getId(),
            organization.getName(),
            organization.getSlug(),
            organization.getDescription(),
            organization.getLogoUrl(),
            organization.getStatus(),
            organization.getCreatedAt(),
            organization.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getDepartments() {
        authorizationService.requirePermission("DEPARTMENT_READ");
        Organization organization = organizationMemberService.getCurrentOrganization();

        return departmentRepository
            .findByOrganizationIdOrderByName(organization.getId())
            .stream()
            .map(this::toDepartmentResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<OrganizationMemberSummaryResponse> getOrganizationMembers() {
        authorizationService.requirePermission("ORGANIZATION_MEMBER_READ");
        Organization organization = organizationMemberService.getCurrentOrganization();

        return organizationMemberRepository
            .findByOrganizationIdWithUserDepartmentRolesAndPermissions(organization.getId())
            .stream()
            .map(this::toOrganizationMemberSummaryResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RoleSummaryResponse> getRoles() {
        authorizationService.requirePermission("ROLE_READ");
        Organization organization = organizationMemberService.getCurrentOrganization();

        return roleRepository
            .findActiveByOrganizationIdWithPermissions(organization.getId())
            .stream()
            .map(this::toRoleSummaryResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<SocialChannelResponse> getSocialChannels() {
        authorizationService.requirePermission("SOCIAL_CHANNEL_READ");
        Organization organization = organizationMemberService.getCurrentOrganization();

        return socialChannelRepository
            .findByOrganizationIdWithAccount(organization.getId())
            .stream()
            .map(this::toSocialChannelResponse)
            .toList();
    }

    private DepartmentResponse toDepartmentResponse(Department department) {
        return new DepartmentResponse(
            department.getId(),
            department.getName(),
            department.getDescription(),
            department.getStatus().name()
        );
    }

    private OrganizationMemberSummaryResponse toOrganizationMemberSummaryResponse(
        OrganizationMember member
    ) {
        List<OrganizationMemberSummaryResponse.RoleInfo> roles = member
            .getRoles()
            .stream()
            .sorted(Comparator.comparing(Role::getName))
            .map(role -> new OrganizationMemberSummaryResponse.RoleInfo(
                role.getId(),
                role.getName(),
                role.getDescription()
            ))
            .toList();

        List<String> permissionCodes = member
            .getRoles()
            .stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getCode)
            .distinct()
            .sorted()
            .toList();

        return new OrganizationMemberSummaryResponse(
            member.getId(),
            member.getStatus().name(),
            new OrganizationMemberSummaryResponse.UserInfo(
                member.getUser().getId(),
                member.getUser().getFirstName(),
                member.getUser().getLastName(),
                member.getUser().getEmail(),
                member.getUser().getStatus().name()
            ),
            new OrganizationMemberSummaryResponse.DepartmentInfo(
                member.getDepartment().getId(),
                member.getDepartment().getName(),
                member.getDepartment().getStatus().name()
            ),
            roles,
            permissionCodes
        );
    }

    private RoleSummaryResponse toRoleSummaryResponse(Role role) {
        List<RoleSummaryResponse.PermissionInfo> permissions = role
            .getPermissions()
            .stream()
            .sorted(Comparator.comparing(Permission::getCode))
            .map(permission -> new RoleSummaryResponse.PermissionInfo(
                permission.getId(),
                permission.getCode(),
                permission.getName(),
                permission.getPermissionGroup().name()
            ))
            .toList();

        return new RoleSummaryResponse(
            role.getId(),
            role.getName(),
            role.getDescription(),
            Boolean.TRUE.equals(role.getActive()),
            new RoleSummaryResponse.DepartmentInfo(
                role.getDepartment().getId(),
                role.getDepartment().getName()
            ),
            permissions
        );
    }

    private SocialChannelResponse toSocialChannelResponse(SocialChannel channel) {
        return new SocialChannelResponse(
            channel.getId(),
            channel.getSocialAccount().getPlatform().name(),
            channel.getSocialAccount().getAccountName(),
            channel.getSocialAccount().getUser().getEmail(),
            channel.getExternalChannelId(),
            channel.getChannelType().name(),
            channel.getChannelName(),
            channel.isCanPublish(),
            channel.getStatus().name()
        );
    }
}
