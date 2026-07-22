package com.example.client;

import com.example.event.PublishRequestedEvent;
import com.example.event.PublishTargetEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TikTokApiClient {

    public PlatformPublishResult publish(
        PublishRequestedEvent event,
        PublishTargetEvent target
    ) {
        String externalPostId =
            "tiktok-" + UUID.randomUUID();

        String externalPostUrl =
            "https://tiktok.com/video/" + externalPostId;

        String responseBody = """
            {
              "success": true,
              "platform": "TIKTOK"
            }
            """;

        return new PlatformPublishResult(
            externalPostId,
            externalPostUrl,
            200,
            responseBody
        );
    }
}