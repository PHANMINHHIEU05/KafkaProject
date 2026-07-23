package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.config.MapStructConfig;
import com.example.dto.PostTargetResponse;
import com.example.entity.PostTarget;
import com.example.entity.SocialAccount;

@Mapper(config = MapStructConfig.class)
public interface PostTargetMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "socialAccountId", source = "socialAccount.id")
    @Mapping(target = "accountName" , source  = "socialAccount.accountName")
    PostTargetResponse toResponse(PostTarget postTarget);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "socialAccount", source = "socialAccount")
    @Mapping(target = "platform", source = "socialAccount.platform")
    @Mapping(target = "socialChannel", ignore = true)
    @Mapping(target = "status", expression = "java(com.example.entity.enums.PublishStatus.PENDING)")
    @Mapping(target = "idempotencyKey", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "externalPostId", ignore = true)
    @Mapping(target = "externalPostUrl", ignore = true)
    @Mapping(target = "errorCode", ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "processingStartedAt", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PostTarget toEntity(SocialAccount socialAccount);
}
