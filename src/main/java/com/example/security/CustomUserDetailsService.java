package com.example.security;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.entity.OrganizationMember;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.entity.enums.OrganizationMemStatus;
import com.example.repository.OrganizationMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final OrganizationMemberRepository organizationMemberRepository;
    @Override
    public UserDetails loadUserByUsername(String email) {
        OrganizationMember member =
            organizationMemberRepository
                .findActiveByUserEmailWithRolesAndPermissions(
                    email,
                    OrganizationMemStatus.ACTIVE
                )
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "Không tìm thấy active member có email: " + email
                    )
                );

        Set<SimpleGrantedAuthority> authorities = new LinkedHashSet<>(); // dùng để đại diện cho quyền hạng , role cho người dùng

        for (Role role : member.getRoles()) {
            authorities.add(
                new SimpleGrantedAuthority("ROLE_" + role.getName())
            );

            for (Permission permission : role.getPermissions()) {
                authorities.add(
                    new SimpleGrantedAuthority(permission.getCode())
                );
            }
        }

        return new org.springframework.security.core.userdetails.User(
            member.getUser().getEmail(),
            member.getUser().getPasswordHash(),
            member.getUser().getStatus()
                == com.example.entity.enums.UserStatus.ACTIVE,
            true,
            true,
            member.getStatus() == OrganizationMemStatus.ACTIVE,
            authorities
        );
    }
}
