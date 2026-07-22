package com.example.client;

import com.example.event.PublishRequestedEvent;
import com.example.event.PublishTargetEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FacebookApiClient {

    public PlatformPublishResult publish(
        PublishRequestedEvent event,
        PublishTargetEvent target
    ) {
        /*
         * Sau này thay đoạn này bằng WebClient gọi Facebook Graph API.
         *
         * MVP hiện tại trả kết quả giả.
         */

        String externalPostId =
            "facebook-" + UUID.randomUUID();

        String externalPostUrl =
            "https://facebook.com/posts/" + externalPostId;

        String responseBody = """
            {
              "success": true,
              "platform": "FACEBOOK"
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