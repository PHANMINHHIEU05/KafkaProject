package com.example.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.entity.Role;
import com.example.exception.ErrorCode;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Set<Role> getByIds(Set<Integer> ids){                                                                                                                                                             
        Set<Role> roles = roleRepository.findByIds(ids);
        if (roles.isEmpty()) {
            throw new ResourceNotFoundException(ErrorCode.ROLES_NOT_FOUND, "Roles not found for ids: " + ids);
        }
        return roles;
    }
}
