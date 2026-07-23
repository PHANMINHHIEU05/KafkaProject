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
    @Mapping(target = "mediaAssetId", source = "mediaAsset.id")
    PostMediaResponse toResponse(PostMedia postMedia);
    
    
    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "post",ignore = true)
    @Mapping(target = "mediaAsset", expression = "java(toMediaAssetReference(request.mediaAssetId()))")
    @Mapping(target = "createdAt", ignore = true)
    PostMedia toEntity(CreatePostMediaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "mediaAsset", expression = "java(toMediaAssetReference(request.mediaAssetId()))")
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(
        CreatePostMediaRequest request,
        @MappingTarget PostMedia entity
    );

    default com.example.entity.MediaAsset toMediaAssetReference(Long id) {
        if (id == null) {
            return null;
        }
        com.example.entity.MediaAsset mediaAsset = new com.example.entity.MediaAsset();
        mediaAsset.setId(id);
        return mediaAsset;
    }
}
