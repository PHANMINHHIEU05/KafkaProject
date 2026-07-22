package com.example.messaging.consumer;

import com.example.event.PublishResultEvent;
import com.example.service.PublishResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublishResultConsumer {

    private final PublishResultService publishResultService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = "post-publish-results",
        groupId = "post-publish-result-group"
    )
    public void consume(
        String message
    ) {
        PublishResultEvent event = readEvent(message);

        if (event == null) {
            log.warn(
                "PublishResultConsumer nhận event null"
            );
            return;
        }

        log.info(
            "Nhận publish result: postId={}, targetId={}, success={}",
            event.postId(),
            event.postTargetId(),
            event.success()
        );

        publishResultService.handleResult(event);
    }

    private PublishResultEvent readEvent(String message) {
        try {
            return objectMapper.readValue(message, PublishResultEvent.class);
        } catch (Exception exception) {
            log.error("PublishResultConsumer không đọc được message: {}", message, exception);
            return null;
        }
    }
}
