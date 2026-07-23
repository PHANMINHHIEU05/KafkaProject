package com.example.event;


import java.time.Instant;
import java.util.List;

public record PublishRequestedEvent(
    Long postId,
    Integer userId,
    String title,
    String content,
    Instant scheduledAt,
    List<PublishMediaEvent> media,
    List<PublishTargetEvent> targets
) {
}