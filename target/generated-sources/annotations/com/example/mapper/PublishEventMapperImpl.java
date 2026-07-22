package com.example.mapper;

import com.example.client.PlatformPublishResult;
import com.example.entity.Post;
import com.example.entity.PostMedia;
import com.example.entity.PostTarget;
import com.example.entity.PublishAttempt;
import com.example.entity.SocialAccount;
import com.example.entity.User;
import com.example.entity.enums.Platform;
import com.example.event.PublishMediaEvent;
import com.example.event.PublishRequestedEvent;
import com.example.event.PublishResultEvent;
import com.example.event.PublishTargetEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-22T09:44:45+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Red Hat, Inc.)"
)
@Component
public class PublishEventMapperImpl implements PublishEventMapper {

    @Override
    public PublishRequestedEvent toPublishRequestedEvent(Post post) {
        if ( post == null ) {
            return null;
        }

        UUID postId = null;
        UUID userId = null;
        String title = null;
        String content = null;
        Instant scheduledAt = null;
        List<PublishMediaEvent> media = null;
        List<PublishTargetEvent> targets = null;

        postId = post.getId();
        userId = postUserId( post );
        title = post.getTitle();
        content = post.getContent();
        scheduledAt = post.getScheduledAt();
        media = postMediaListToPublishMediaEventList( post.getMedia() );
        targets = postTargetListToPublishTargetEventList( post.getTargets() );

        PublishRequestedEvent publishRequestedEvent = new PublishRequestedEvent( postId, userId, title, content, scheduledAt, media, targets );

        return publishRequestedEvent;
    }

    @Override
    public PublishMediaEvent toPublishMediaEvent(PostMedia media) {
        if ( media == null ) {
            return null;
        }

        UUID mediaId = null;
        String mediaUrl = null;
        String mimeType = null;
        String thumbnailUrl = null;
        Integer sortOrder = null;

        mediaId = media.getId();
        mediaUrl = media.getMediaUrl();
        mimeType = media.getMimeType();
        thumbnailUrl = media.getThumbnailUrl();
        sortOrder = media.getSortOrder();

        Object mediaType = media.getMediaType() == null ? null : media.getMediaType().name();

        PublishMediaEvent publishMediaEvent = new PublishMediaEvent( mediaId, mediaType, mediaUrl, mimeType, thumbnailUrl, sortOrder );

        return publishMediaEvent;
    }

    @Override
    public PublishTargetEvent toPublishTargetEvent(PostTarget target) {
        if ( target == null ) {
            return null;
        }

        UUID postTargetId = null;
        UUID socialAccountId = null;
        Platform platform = null;
        String idempotencyKey = null;

        postTargetId = target.getId();
        socialAccountId = targetSocialAccountId( target );
        platform = target.getPlatform();
        idempotencyKey = target.getIdempotencyKey();

        PublishTargetEvent publishTargetEvent = new PublishTargetEvent( postTargetId, socialAccountId, platform, idempotencyKey );

        return publishTargetEvent;
    }

    @Override
    public PublishResultEvent toSuccessResultEvent(PublishRequestedEvent event, PublishTargetEvent target, PublishAttempt attempt, Platform platform, PlatformPublishResult result) {
        if ( event == null && target == null && attempt == null && platform == null && result == null ) {
            return null;
        }

        UUID postId = null;
        if ( event != null ) {
            postId = event.postId();
        }
        UUID postTargetId = null;
        if ( target != null ) {
            postTargetId = target.postTargetId();
        }
        Long publishAttemptId = null;
        if ( attempt != null ) {
            publishAttemptId = attempt.getId();
        }
        String externalPostId = null;
        String externalPostUrl = null;
        Integer httpStatusCode = null;
        if ( result != null ) {
            externalPostId = result.externalPostId();
            externalPostUrl = result.externalPostUrl();
            httpStatusCode = result.httpStatusCode();
        }
        Platform platform1 = null;
        platform1 = platform;

        boolean success = true;
        String errorCode = null;
        String errorMessage = null;
        boolean retryable = false;
        Instant occurredAt = java.time.Instant.now();

        PublishResultEvent publishResultEvent = new PublishResultEvent( postId, postTargetId, publishAttemptId, platform1, success, externalPostId, externalPostUrl, httpStatusCode, errorCode, errorMessage, retryable, occurredAt );

        return publishResultEvent;
    }

    @Override
    public PublishResultEvent toFailureResultEvent(PublishRequestedEvent event, PublishTargetEvent target, PublishAttempt attempt, Platform platform, String errorCode, String errorMessage, boolean retryable) {
        if ( event == null && target == null && attempt == null && platform == null && errorCode == null && errorMessage == null ) {
            return null;
        }

        UUID postId = null;
        if ( event != null ) {
            postId = event.postId();
        }
        UUID postTargetId = null;
        if ( target != null ) {
            postTargetId = target.postTargetId();
        }
        Long publishAttemptId = null;
        if ( attempt != null ) {
            publishAttemptId = attempt.getId();
        }
        Platform platform1 = null;
        platform1 = platform;
        String errorCode1 = null;
        errorCode1 = errorCode;
        String errorMessage1 = null;
        errorMessage1 = errorMessage;
        boolean retryable1 = false;
        retryable1 = retryable;

        boolean success = false;
        String externalPostId = null;
        String externalPostUrl = null;
        Integer httpStatusCode = null;
        Instant occurredAt = java.time.Instant.now();

        PublishResultEvent publishResultEvent = new PublishResultEvent( postId, postTargetId, publishAttemptId, platform1, success, externalPostId, externalPostUrl, httpStatusCode, errorCode1, errorMessage1, retryable1, occurredAt );

        return publishResultEvent;
    }

    private UUID postUserId(Post post) {
        if ( post == null ) {
            return null;
        }
        User user = post.getUser();
        if ( user == null ) {
            return null;
        }
        UUID id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<PublishMediaEvent> postMediaListToPublishMediaEventList(List<PostMedia> list) {
        if ( list == null ) {
            return null;
        }

        List<PublishMediaEvent> list1 = new ArrayList<PublishMediaEvent>( list.size() );
        for ( PostMedia postMedia : list ) {
            list1.add( toPublishMediaEvent( postMedia ) );
        }

        return list1;
    }

    protected List<PublishTargetEvent> postTargetListToPublishTargetEventList(List<PostTarget> list) {
        if ( list == null ) {
            return null;
        }

        List<PublishTargetEvent> list1 = new ArrayList<PublishTargetEvent>( list.size() );
        for ( PostTarget postTarget : list ) {
            list1.add( toPublishTargetEvent( postTarget ) );
        }

        return list1;
    }

    private UUID targetSocialAccountId(PostTarget postTarget) {
        if ( postTarget == null ) {
            return null;
        }
        SocialAccount socialAccount = postTarget.getSocialAccount();
        if ( socialAccount == null ) {
            return null;
        }
        UUID id = socialAccount.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
