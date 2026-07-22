package com.example.messaging.publisher;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.entity.OutBox;
import com.example.service.OutboxService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {
    private static final int BATCH_SIZE = 20;
    private final OutboxService outboxService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Scheduled(fixedDelayString = "${outbox.publisher.fixed-delay-ms:5000}")
    public void publishReadyEvents(){
        List<OutBox> events = outboxService.claimReadyEvents(BATCH_SIZE);
        if(events.isEmpty()){
            log.info("Không có OutboxEvent nào sẵn sàng để xuất bản");
            return;
        }
        for(OutBox event : events){
            publishEvent(event);
        }
    }
    
    private void publishEvent(OutBox event){
        try{
            kafkaTemplate.send(event.getTopic(), event.getEventKey(), event.getPayload().toString()).get(10 , TimeUnit.SECONDS);
            outboxService.markPublished(event.getId());
            log.info(
                "Gửi Kafka thành công: eventId={}, topic={}, key={}",
                event.getId(),
                event.getTopic(),
                event.getEventKey()
            );
        } catch (Exception e) {
            String errorMessage =
                extractErrorMessage(e);

            log.error(
                "Gửi Kafka thất bại: eventId={}, topic={}, error={}",
                event.getId(),
                event.getTopic(),
                errorMessage,
                e
            );

            outboxService.markFailed(
                event.getId(),
                "KAFKA_PUBLISH_FAILED",
                errorMessage
            );
        }
    }
    private String extractErrorMessage(Exception exception) {
        Throwable cause = exception.getCause();

        if (cause != null
            && cause.getMessage() != null
            && !cause.getMessage().isBlank()) {

            return limitMessage(cause.getMessage());
        }

        if (exception.getMessage() != null
            && !exception.getMessage().isBlank()) {

            return limitMessage(exception.getMessage());
        }

        return exception.getClass().getSimpleName();
    }

    private String limitMessage(String message) {
        int maxLength = 2000;

        if (message.length() <= maxLength) {
            return message;
        }

        return message.substring(0, maxLength);
    }
}
