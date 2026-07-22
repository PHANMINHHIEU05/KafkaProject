package com.example.event;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PublishRequestedEvent(
    UUID postId,
    UUID userId,
    String title,
    String content,
    Instant scheduledAt,
    List<PublishMediaEvent> media,
    List<PublishTargetEvent> targets
) {
}