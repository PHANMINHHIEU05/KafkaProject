package com.example.mapper;

import com.example.dto.CreatePostMediaRequest;
import com.example.dto.PostMediaResponse;
import com.example.entity.PostMedia;
import com.example.entity.enums.MediaType;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-22T08:20:40+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Red Hat, Inc.)"
)
@Component
public class PostMediaMapperImpl implements PostMediaMapper {

    @Override
    public PostMediaResponse toResponse(PostMedia postMedia) {
        if ( postMedia == null ) {
            return null;
        }

        UUID id = null;
        MediaType mediaType = null;
        String mediaUrl = null;
        String mimeType = null;
        String thumbnailUrl = null;
        Integer sortOrder = null;

        id = postMedia.getId();
        mediaType = postMedia.getMediaType();
        mediaUrl = postMedia.getMediaUrl();
        mimeType = postMedia.getMimeType();
        thumbnailUrl = postMedia.getThumbnailUrl();
        sortOrder = postMedia.getSortOrder();

        PostMediaResponse postMediaResponse = new PostMediaResponse( id, mediaType, mediaUrl, mimeType, thumbnailUrl, sortOrder );

        return postMediaResponse;
    }

    @Override
    public PostMedia toEntity(CreatePostMediaRequest request) {
        if ( request == null ) {
            return null;
        }

        PostMedia.PostMediaBuilder postMedia = PostMedia.builder();

        postMedia.mediaType( request.mediaType() );
        postMedia.mediaUrl( request.mediaUrl() );
        postMedia.mimeType( request.mimeType() );
        postMedia.thumbnailUrl( request.thumbnailUrl() );
        postMedia.sortOrder( request.sortOrder() );

        return postMedia.build();
    }

    @Override
    public void updateEntity(CreatePostMediaRequest request, PostMedia entity) {
        if ( request == null ) {
            return;
        }

        entity.setMediaType( request.mediaType() );
        entity.setMediaUrl( request.mediaUrl() );
        entity.setMimeType( request.mimeType() );
        entity.setThumbnailUrl( request.thumbnailUrl() );
        entity.setSortOrder( request.sortOrder() );
    }
}
