package com.example.mapper;

import com.example.dto.CreatePostRequest;
import com.example.dto.PostMediaResponse;
import com.example.dto.PostResponse;
import com.example.dto.PostSummaryResponse;
import com.example.dto.PostTargetResponse;
import com.example.entity.Post;
import com.example.entity.PostMedia;
import com.example.entity.PostTarget;
import com.example.entity.User;
import com.example.entity.enums.PostStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-23T22:58:52+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Red Hat, Inc.)"
)
@Component
public class PostMapperImpl implements PostMapper {

    @Autowired
    private PostMediaMapper postMediaMapper;
    @Autowired
    private PostTargetMapper postTargetMapper;

    @Override
    public Post toEntity(CreatePostRequest request) {
        if ( request == null ) {
            return null;
        }

        Post.PostBuilder post = Post.builder();

        post.title( request.title() );
        post.content( request.content() );
        post.clientRequestId( request.clientRequestId() );
        post.scheduledAt( request.scheduledAt() );

        return post.build();
    }

    @Override
    public PostResponse toResponse(Post post) {
        if ( post == null ) {
            return null;
        }

        Integer userId = null;
        Long id = null;
        String title = null;
        String content = null;
        PostStatus status = null;
        String clientRequestId = null;
        Instant scheduledAt = null;
        Long version = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        List<PostMediaResponse> media = null;
        List<PostTargetResponse> targets = null;

        userId = postUserId( post );
        id = post.getId();
        title = post.getTitle();
        content = post.getContent();
        status = post.getStatus();
        clientRequestId = post.getClientRequestId();
        scheduledAt = post.getScheduledAt();
        version = post.getVersion();
        createdAt = post.getCreatedAt();
        updatedAt = post.getUpdatedAt();
        media = postMediaListToPostMediaResponseList( post.getMedia() );
        targets = postTargetListToPostTargetResponseList( post.getTargets() );

        PostResponse postResponse = new PostResponse( id, userId, title, content, status, clientRequestId, scheduledAt, version, createdAt, updatedAt, media, targets );

        return postResponse;
    }

    @Override
    public PostSummaryResponse toSummaryResponse(Post post) {
        if ( post == null ) {
            return null;
        }

        Long id = null;
        String title = null;
        String content = null;
        PostStatus status = null;
        String clientRequestId = null;
        Instant scheduledAt = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        id = post.getId();
        title = post.getTitle();
        content = post.getContent();
        status = post.getStatus();
        clientRequestId = post.getClientRequestId();
        scheduledAt = post.getScheduledAt();
        createdAt = post.getCreatedAt();
        updatedAt = post.getUpdatedAt();

        long targetCount = 0L;
        long publishedTargetCount = 0L;
        long failedTargetCount = 0L;

        PostSummaryResponse postSummaryResponse = new PostSummaryResponse( id, title, content, status, clientRequestId, scheduledAt, targetCount, publishedTargetCount, failedTargetCount, createdAt, updatedAt );

        return postSummaryResponse;
    }

    @Override
    public void updateEntity(CreatePostRequest request, Post post) {
        if ( request == null ) {
            return;
        }

        post.setTitle( request.title() );
        post.setContent( request.content() );
        post.setClientRequestId( request.clientRequestId() );
        post.setScheduledAt( request.scheduledAt() );
    }

    private Integer postUserId(Post post) {
        if ( post == null ) {
            return null;
        }
        User user = post.getUser();
        if ( user == null ) {
            return null;
        }
        int id = user.getId();
        return id;
    }

    protected List<PostMediaResponse> postMediaListToPostMediaResponseList(List<PostMedia> list) {
        if ( list == null ) {
            return null;
        }

        List<PostMediaResponse> list1 = new ArrayList<PostMediaResponse>( list.size() );
        for ( PostMedia postMedia : list ) {
            list1.add( postMediaMapper.toResponse( postMedia ) );
        }

        return list1;
    }

    protected List<PostTargetResponse> postTargetListToPostTargetResponseList(List<PostTarget> list) {
        if ( list == null ) {
            return null;
        }

        List<PostTargetResponse> list1 = new ArrayList<PostTargetResponse>( list.size() );
        for ( PostTarget postTarget : list ) {
            list1.add( postTargetMapper.toResponse( postTarget ) );
        }

        return list1;
    }
}
