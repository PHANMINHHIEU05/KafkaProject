package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.config.MapStructConfig;
import com.example.dto.PostTargetResponse;
import com.example.entity.PostTarget;

@Mapper(config = MapStructConfig.class)
public interface PostTargetMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "socialAccountId", source = "socialAccount.id")
    @Mapping(target = "accountName" , source  = "socialAccount.accountName")
    PostTargetResponse toResponse(PostTarget postTarget);
}
