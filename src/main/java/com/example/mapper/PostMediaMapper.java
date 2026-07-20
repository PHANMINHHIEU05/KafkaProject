package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.config.MapStructConfig;
import com.example.dto.CreatePostMediaRequest;
import com.example.dto.PostMediaResponse;
import com.example.entity.PostMedia;

@Mapper(config = MapStructConfig.class)
public interface PostMediaMapper {
    @Mapping(target = "postId" , source = "post.id")
    PostMediaResponse toResponse(PostMedia postMedia);
    
    
    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "post",ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PostMedia toEntity(CreatePostMediaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
        CreatePostMediaRequest request,
        @MappingTarget PostMedia entity
    );
}
