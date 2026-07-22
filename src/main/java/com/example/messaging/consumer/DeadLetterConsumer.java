package com.example.messaging.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;

@Slf4j
@Component
public class DeadLetterConsumer {
    @KafkaListener(
        topics = {
            "post-publish-requests.DLT",
            "post-publish-results.DLT"
        },
        groupId = "post-publish-dlt-group"
    )
    public void consume(ConsumerRecord<String , JsonNode> record) {
        log.error(
            "DeadLetterConsumer nhận message từ topic {}: key={}, value={}",
            record.topic(),
            record.key(),
            record.value(),
            record.partition(),
            record.offset()
        );
    }   
}
