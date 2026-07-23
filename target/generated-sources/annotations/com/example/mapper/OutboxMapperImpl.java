package com.example.mapper;

import com.example.entity.OutBox;
import com.example.entity.Post;
import com.example.event.PublishRequestedEvent;
import com.example.event.PublishResultEvent;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-23T22:58:52+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Red Hat, Inc.)"
)
@Component
public class OutboxMapperImpl implements OutboxMapper {

    @Override
    public OutBox toPublishRequestedOutbox(Post post, PublishRequestedEvent event, ObjectMapper objectMapper) {
        if ( post == null && event == null ) {
            return null;
        }

        OutBox.OutBoxBuilder outBox = OutBox.builder();

        if ( post != null ) {
            outBox.aggregateId( post.getId() );
            outBox.organization( post.getOrganization() );
            outBox.updatedAt( post.getUpdatedAt() );
        }
        outBox.aggregateType( "POST" );
        outBox.eventType( "POST_PUBLISH_REQUESTED" );
        outBox.topic( "post-publish-requests" );
        outBox.eventKey( post.getId().toString() );
        outBox.payload( objectMapper.valueToTree(event) );
        outBox.status( com.example.entity.enums.OutboxStatus.NEW );
        outBox.retryCount( 0 );
        outBox.maxRetry( 10 );
        outBox.availableAt( post.getScheduledAt() == null ? java.time.Instant.now() : post.getScheduledAt() );

        return outBox.build();
    }

    @Override
    public OutBox toPublishResultOutbox(PublishResultEvent result, ObjectMapper objectMapper) {
        if ( result == null ) {
            return null;
        }

        OutBox.OutBoxBuilder outBox = OutBox.builder();

        outBox.aggregateId( result.postId() );

        outBox.aggregateType( "POST" );
        outBox.eventType( "POST_PUBLISH_RESULT" );
        outBox.topic( "post-publish-results" );
        outBox.eventKey( result.postId().toString() );
        outBox.payload( objectMapper.valueToTree(result) );
        outBox.status( com.example.entity.enums.OutboxStatus.NEW );
        outBox.retryCount( 0 );
        outBox.maxRetry( 10 );
        outBox.availableAt( java.time.Instant.now() );

        return outBox.build();
    }
}
