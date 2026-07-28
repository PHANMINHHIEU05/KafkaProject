package com.example.dto;

public record DepartmentResponse(
    int id,
    String name,
    String description,
    String status
) {
}
