package com.example.service;

import com.example.entity.OutBox;
import com.example.entity.enums.OutboxStatus;
import com.example.event.PublishResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PublishResultOutboxService {

    private static final String RESULT_TOPIC =
        "post-publish-results";

    private static final String RESULT_EVENT_TYPE =
        "POST_PUBLISH_RESULT";

    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    public OutBox saveResult(
        PublishResultEvent result
    ) {
        JsonNode payload =
            objectMapper.valueToTree(result);

        OutBox event = OutBox.builder()
            /*
             * aggregate vẫn là Post.
             */
            .aggregateId(result.postId())
            .aggregateType("POST")
            .eventType(RESULT_EVENT_TYPE)
            .topic(RESULT_TOPIC)
            .eventKey(result.postId().toString())
            .payload(payload)
            .status(OutboxStatus.NEW)
            .retryCount(0)
            .maxRetry(10)
            .availableAt(Instant.now())
            .build();

        return outboxService.save(event);
    }
}
