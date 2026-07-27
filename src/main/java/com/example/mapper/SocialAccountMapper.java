package com.example.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapper;

import com.example.config.MapStructConfig;
import com.example.dto.CreateSocialAccountRequest;
import com.example.dto.SocialAccountResponse;
import com.example.entity.SocialAccount;

@Mapper(config = MapStructConfig.class)
public interface SocialAccountMapper {
    @Mapping(target = "userId", source = "user.id")
    SocialAccountResponse toResponse(SocialAccount socialAccount);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "connectionStatus", ignore = true)
    @Mapping(target = "connectedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "accessTokenEncrypted", ignore = true)
    @Mapping(target = "refreshTokenEncrypted", ignore = true)
    @Mapping(target = "tokenExpiresAt", ignore = true)
    @Mapping(target = "lastSyncedAt", ignore = true)
    SocialAccount toEntity(
        CreateSocialAccountRequest request
    );
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "connectionStatus", ignore = true)
    @Mapping(target = "connectedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "accessTokenEncrypted", ignore = true)
    @Mapping(target = "refreshTokenEncrypted", ignore = true)
    @Mapping(target = "tokenExpiresAt", ignore = true)
    @Mapping(target = "lastSyncedAt", ignore = true)
    void updateEntity(
        CreateSocialAccountRequest request,
        @MappingTarget SocialAccount entity
    );
}
