package com.example.dto;

import com.example.entity.enums.DepartmentStatus;

public record InnerDepartmentResponse(
    int id ,
    String name,
    String description,
    DepartmentStatus status
) {
}  
