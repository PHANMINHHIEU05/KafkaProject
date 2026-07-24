package com.example.service;

import org.springframework.stereotype.Service;

import com.example.entity.Department;
import com.example.entity.Organization;
import com.example.exception.ErrorCode;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final  DepartmentRepository departmentRepository;
    private final OrganizationMemberService organizationMemberService;
    public Department getById(Integer departmentId){
        Organization origanization = organizationMemberService.getCurrentOrganization();
        Department result = departmentRepository.findByIdAndOrgId(departmentId, origanization.getId());
        if(result == null){
            throw new ResourceNotFoundException(ErrorCode.DEPARTMENT_NOT_FOUND , "Department with id " + departmentId + " not found");
        }
        return result;
    }
}
