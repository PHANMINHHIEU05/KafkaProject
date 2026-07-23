package com.example.mapper;

import com.example.dto.CreatePostMediaRequest;
import com.example.dto.PostMediaResponse;
import com.example.entity.MediaAsset;
import com.example.entity.PostMedia;
import com.example.entity.enums.MediaType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-23T22:58:52+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Red Hat, Inc.)"
)
@Component
public class PostMediaMapperImpl implements PostMediaMapper {

    @Override
    public PostMediaResponse toResponse(PostMedia postMedia) {
        if ( postMedia == null ) {
            return null;
        }

        Long mediaAssetId = null;
        Long id = null;
        MediaType mediaType = null;
        String mimeType = null;
        Integer sortOrder = null;

        mediaAssetId = postMediaMediaAssetId( postMedia );
        id = postMedia.getId();
        mediaType = postMedia.getMediaType();
        mimeType = postMedia.getMimeType();
        sortOrder = postMedia.getSortOrder();

        PostMediaResponse postMediaResponse = new PostMediaResponse( id, mediaAssetId, mediaType, mimeType, sortOrder );

        return postMediaResponse;
    }

    @Override
    public PostMedia toEntity(CreatePostMediaRequest request) {
        if ( request == null ) {
            return null;
        }

        PostMedia.PostMediaBuilder postMedia = PostMedia.builder();

        postMedia.sortOrder( request.sortOrder() );

        postMedia.mediaAsset( toMediaAssetReference(request.mediaAssetId()) );

        return postMedia.build();
    }

    @Override
    public void updateEntity(CreatePostMediaRequest request, PostMedia entity) {
        if ( request == null ) {
            return;
        }

        entity.setSortOrder( request.sortOrder() );

        entity.setMediaAsset( toMediaAssetReference(request.mediaAssetId()) );
    }

    private Long postMediaMediaAssetId(PostMedia postMedia) {
        if ( postMedia == null ) {
            return null;
        }
        MediaAsset mediaAsset = postMedia.getMediaAsset();
        if ( mediaAsset == null ) {
            return null;
        }
        Long id = mediaAsset.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
