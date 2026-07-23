package com.example.mapper;

import com.example.dto.PostTargetResponse;
import com.example.entity.Post;
import com.example.entity.PostTarget;
import com.example.entity.SocialAccount;
import com.example.entity.enums.Platform;
import com.example.entity.enums.PublishStatus;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-23T22:58:52+0700",
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

        PostTargetResponse postTargetResponse = new PostTargetResponse( id, postId, socialAccountId, accountName, platform, status, externalPostId, errorCode, errorMessage, processingStartedAt, publishedAt );

        return postTargetResponse;
    }

    @Override
    public PostTarget toEntity(SocialAccount socialAccount) {
        if ( socialAccount == null ) {
            return null;
        }

        PostTarget.PostTargetBuilder postTarget = PostTarget.builder();

        postTarget.socialAccount( socialAccount );
        postTarget.platform( socialAccount.getPlatform() );

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
