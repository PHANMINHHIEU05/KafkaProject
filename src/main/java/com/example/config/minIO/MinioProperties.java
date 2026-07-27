package com.example.config.minIO;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "storage.minio")
public record MinioProperties(
    @NotBlank
    String endpoint,
    @NotBlank
    String accessKey,
    @NotBlank
    String secretKey,
    @NotBlank
    String bucket,
    @NotNull
    Integer uploadUrlExpirySeconds,
    @NotNull
    Integer downloadUrlExpirySeconds,
    @NotNull
    Long maxImageSizeBytes,
    @NotNull
    Long maxVideoSizeBytes  ) {
}
