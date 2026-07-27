package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.config.MapStructConfig;
import com.example.dto.UserResponse;
import com.example.entity.User;

@Mapper(
        config = MapStructConfig.class,
        uses = {
                SocialAccountMapper.class,
                PostMapper.class
        }
)
public interface UserMapper {
    @Mapping(target = "organizationMember", ignore = true)
    UserResponse toResponse(User user);
}
