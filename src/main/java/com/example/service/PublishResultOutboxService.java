package com.example.service;

import com.example.entity.OutBox;
import com.example.event.PublishResultEvent;
import com.example.mapper.OutboxMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class PublishResultOutboxService {

    private static final String RESULT_TOPIC =
        "post-publish-results";

    private static final String RESULT_EVENT_TYPE =
        "POST_PUBLISH_RESULT";

    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;
    private final OutboxMapper outboxMapper;

    public OutBox saveResult(
        PublishResultEvent result
    ) {
        OutBox event = outboxMapper.toPublishResultOutbox(
            result,
            objectMapper
        );

        return outboxService.save(event);
    }
}
