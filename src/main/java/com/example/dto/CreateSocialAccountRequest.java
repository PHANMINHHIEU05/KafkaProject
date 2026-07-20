package com.example.dto;

import com.example.entity.Platform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSocialAccountRequest(

    @NotNull(message = "Nền tảng không được để trống")
    Platform platform,

    @NotBlank(message = "External account ID không được để trống")
    @Size(max = 255)
    String externalAccountId,

    @NotBlank(message = "Tên tài khoản không được để trống")
    @Size(max = 255)
    String accountName

) {
}