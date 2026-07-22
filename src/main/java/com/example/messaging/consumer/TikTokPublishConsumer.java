package com.example.messaging.consumer;

import com.example.entity.enums.Platform;
import com.example.event.PublishRequestedEvent;
import com.example.service.TikTokPublishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class TikTokPublishConsumer {

    private final TikTokPublishService tikTokPublishService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = "post-publish-requests",
        groupId = "tiktok-publish-group"
    )
    public void consume(String message) {
        PublishRequestedEvent event = readEvent(message);

        if (event == null) {
            log.warn("TikTok consumer nhận event null");
            return;
        }

        if (event.targets() == null || event.targets().isEmpty()) {
            log.warn(
                "TikTok consumer nhận event không có target: postId={}",
                event.postId()
            );
            return;
        }

        event.targets()
            .stream()
            .filter(target ->
                target.platform() == Platform.TIKTOK
            )
            .forEach(target ->
                tikTokPublishService.publish(event, target)
            );
    }

    private PublishRequestedEvent readEvent(String message) {
        try {
            return objectMapper.readValue(message, PublishRequestedEvent.class);
        } catch (Exception exception) {
            log.error("TikTok consumer không đọc được message: {}", message, exception);
            return null;
        }
    }
}
