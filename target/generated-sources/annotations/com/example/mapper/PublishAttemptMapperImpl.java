package com.example.mapper;

import com.example.dto.PublishAttemptResponse;
import com.example.entity.PostTarget;
import com.example.entity.PublishAttempt;
import com.example.entity.enums.AttemptStatus;
import com.example.entity.enums.PublishAttemptStatus;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-22T08:20:40+0700",
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
        UUID id = null;
        Integer attemptNumber = null;
        AttemptStatus status = null;
        Integer httpStatusCode = null;
        boolean retryable = false;
        String errorCode = null;
        String errorMessage = null;
        Instant startedAt = null;
        Instant finishedAt = null;

        postTargetId = entityPostTargetId( entity );
        id = entity.getId();
        attemptNumber = entity.getAttemptNumber();
        status = publishAttemptStatusToAttemptStatus( entity.getStatus() );
        httpStatusCode = entity.getHttpStatusCode();
        if ( entity.getRetryable() != null ) {
            retryable = entity.getRetryable();
        }
        errorCode = entity.getErrorCode();
        errorMessage = entity.getErrorMessage();
        startedAt = entity.getStartedAt();
        finishedAt = entity.getFinishedAt();

        String requestId = null;

        PublishAttemptResponse publishAttemptResponse = new PublishAttemptResponse( id, postTargetId, attemptNumber, status, requestId, httpStatusCode, retryable, errorCode, errorMessage, startedAt, finishedAt );

        return publishAttemptResponse;
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

    protected AttemptStatus publishAttemptStatusToAttemptStatus(PublishAttemptStatus publishAttemptStatus) {
        if ( publishAttemptStatus == null ) {
            return null;
        }

        AttemptStatus attemptStatus;

        switch ( publishAttemptStatus ) {
            case PROCESSING: attemptStatus = AttemptStatus.PROCESSING;
            break;
            case SUCCESS: attemptStatus = AttemptStatus.SUCCESS;
            break;
            case FAILED: attemptStatus = AttemptStatus.FAILED;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + publishAttemptStatus );
        }

        return attemptStatus;
    }
}
