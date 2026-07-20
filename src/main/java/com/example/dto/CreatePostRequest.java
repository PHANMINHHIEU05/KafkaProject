package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import java.time.Instant;
import java.util.List;
public record CreatePostRequest(
    @Size(max = 225 , message = "không quá 225 kí tự")
    String title,
    @Size(max = 1000 , message = "không quá 1000 kí tự")
    @NotBlank(message = "Nội dung không được để trống")
    String content,
    @NotEmpty(message = "Danh sách media không được để trống")
    List<UUID> socialAccountIds,
    List<CreatePostMediaRequest> mediaList,
    @NotEmpty
    String clientRequestId,
    Instant scheduledAt
) {
    
}
