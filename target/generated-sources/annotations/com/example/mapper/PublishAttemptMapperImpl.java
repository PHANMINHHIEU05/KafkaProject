package com.example.mapper;

import com.example.dto.PublishAttemptResponse;
import com.example.entity.PostTarget;
import com.example.entity.PublishAttempt;
import com.example.entity.enums.AttemptStatus;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-22T09:44:45+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Red Hat, Inc.)"
)
@Component
public class PublishAttemptMapperImpl implements PublishAttemptMapper {

    @Override
    public PublishAttemptResponse toResponse(PublishAttempt entity) {
        if ( entity == null ) {
            return null;
        }

        UUID postTargetId = null;
        Long id = null;
        Integer attemptNumber = null;
        AttemptStatus status = null;
        String requestId = null;
        Integer httpStatusCode = null;
        boolean retryable = false;
        String errorCode = null;
        String errorMessage = null;
        Instant startedAt = null;
        Instant finishedAt = null;

        postTargetId = entityPostTargetId( entity );
        id = entity.getId();
        attemptNumber = entity.getAttemptNumber();
        status = entity.getStatus();
        requestId = entity.getRequestId();
        httpStatusCode = entity.getHttpStatusCode();
        if ( entity.getRetryable() != null ) {
            retryable = entity.getRetryable();
        }
        errorCode = entity.getErrorCode();
        errorMessage = entity.getErrorMessage();
        startedAt = entity.getStartedAt();
        finishedAt = entity.getFinishedAt();

        PublishAttemptResponse publishAttemptResponse = new PublishAttemptResponse( id, postTargetId, attemptNumber, status, requestId, httpStatusCode, retryable, errorCode, errorMessage, startedAt, finishedAt );

        return publishAttemptResponse;
    }

    @Override
    public PublishAttempt toProcessingAttempt(PostTarget postTarget, Integer attemptNumber, String requestId) {
        if ( postTarget == null && attemptNumber == null && requestId == null ) {
            return null;
        }

        PublishAttempt.PublishAttemptBuilder publishAttempt = PublishAttempt.builder();

        publishAttempt.postTarget( postTarget );
        publishAttempt.attemptNumber( attemptNumber );
        publishAttempt.requestId( requestId );
        publishAttempt.status( com.example.entity.enums.AttemptStatus.PROCESSING );
        publishAttempt.retryable( false );
        publishAttempt.startedAt( java.time.Instant.now() );

        return publishAttempt.build();
    }

    private UUID entityPostTargetId(PublishAttempt publishAttempt) {
        if ( publishAttempt == null ) {
            return null;
        }
        PostTarget postTarget = publishAttempt.getPostTarget();
        if ( postTarget == null ) {
            return null;
        }
        UUID id = postTarget.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
