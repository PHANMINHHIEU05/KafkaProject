package com.example.mapper;

import com.example.dto.PostTargetResponse;
import com.example.entity.Post;
import com.example.entity.PostTarget;
import com.example.entity.SocialAccount;
import com.example.entity.SocialChannel;
import com.example.entity.enums.Platform;
import com.example.entity.enums.PublishStatus;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-27T14:01:32+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Red Hat, Inc.)"
)
@Component
public class PostTargetMapperImpl implements PostTargetMapper {

    @Override
    public PostTargetResponse toResponse(PostTarget postTarget) {
        if ( postTarget == null ) {
            return null;
        }

        Long postId = null;
        Integer socialChannelId = null;
        Integer socialAccountId = null;
        String accountName = null;
        Long id = null;
        Platform platform = null;
        PublishStatus status = null;
        String externalPostId = null;
        String errorCode = null;
        String errorMessage = null;
        Instant processingStartedAt = null;
        Instant publishedAt = null;

        postId = postTargetPostId( postTarget );
        socialChannelId = postTargetSocialChannelId( postTarget );
        socialAccountId = postTargetSocialAccountId( postTarget );
        accountName = postTargetSocialAccountAccountName( postTarget );
        id = postTarget.getId();
        platform = postTarget.getPlatform();
        status = postTarget.getStatus();
        externalPostId = postTarget.getExternalPostId();
        errorCode = postTarget.getErrorCode();
        errorMessage = postTarget.getErrorMessage();
        processingStartedAt = postTarget.getProcessingStartedAt();
        publishedAt = postTarget.getPublishedAt();

        PostTargetResponse postTargetResponse = new PostTargetResponse( id, postId, socialChannelId, socialAccountId, accountName, platform, status, externalPostId, errorCode, errorMessage, processingStartedAt, publishedAt );

        return postTargetResponse;
    }

    @Override
    public PostTarget toEntity(SocialChannel socialChannel) {
        if ( socialChannel == null ) {
            return null;
        }

        PostTarget.PostTargetBuilder postTarget = PostTarget.builder();

        postTarget.socialChannel( socialChannel );

        postTarget.status( com.example.entity.enums.PublishStatus.PENDING );
        postTarget.idempotencyKey( java.util.UUID.randomUUID().toString() );

        return postTarget.build();
    }

    private Long postTargetPostId(PostTarget postTarget) {
        if ( postTarget == null ) {
            return null;
        }
        Post post = postTarget.getPost();
        if ( post == null ) {
            return null;
        }
        Long id = post.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Integer postTargetSocialChannelId(PostTarget postTarget) {
        if ( postTarget == null ) {
            return null;
        }
        SocialChannel socialChannel = postTarget.getSocialChannel();
        if ( socialChannel == null ) {
            return null;
        }
        int id = socialChannel.getId();
        return id;
    }

    private Integer postTargetSocialAccountId(PostTarget postTarget) {
        if ( postTarget == null ) {
            return null;
        }
        SocialAccount socialAccount = postTarget.getSocialAccount();
        if ( socialAccount == null ) {
            return null;
        }
        int id = socialAccount.getId();
        return id;
    }

    private String postTargetSocialAccountAccountName(PostTarget postTarget) {
        if ( postTarget == null ) {
            return null;
        }
        SocialAccount socialAccount = postTarget.getSocialAccount();
        if ( socialAccount == null ) {
            return null;
        }
        String accountName = socialAccount.getAccountName();
        if ( accountName == null ) {
            return null;
        }
        return accountName;
    }
}
