package com.example.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapper;

import com.example.config.MapStructConfig;
import com.example.dto.CreateSocialAccountRequest;
import com.example.entity.SocialAccount;

@Mapper(config = MapStructConfig.class)
public interface SocialAccountMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "connectionStatus", ignore = true)
    @Mapping(target = "connectedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SocialAccount toEntity(
        CreateSocialAccountRequest request
    );
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "connectionStatus", ignore = true)
    @Mapping(target = "connectedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
        CreateSocialAccountRequest request,
        @MappingTarget SocialAccount entity
    );
}
