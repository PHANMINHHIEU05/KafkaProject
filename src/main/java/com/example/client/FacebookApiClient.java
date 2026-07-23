package com.example.client;

import com.example.event.PublishRequestedEvent;
import com.example.event.PublishTargetEvent;
import com.example.exception.ApiException;
import com.example.exception.ErrorCode;

import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.util.UUID;

@Component
public class FacebookApiClient {

    public PlatformPublishResult publish(
        PublishRequestedEvent event,
        PublishTargetEvent target
    ) {
        simulateError(event);

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

    private void simulateError(
        PublishRequestedEvent event
    ) {
        String content = event.content();

        if (content == null) {
            return;
        }

        if (content.contains("[FB_TIMEOUT]")) {
            throw new ApiException(
                ErrorCode.FACEBOOK_TIMEOUT,
                "Facebook API bị timeout",
                504,
                true
            );
        }

        if (content.contains("[FB_503]")) {
            throw new ApiException(
                ErrorCode.FACEBOOK_SERVICE_UNAVAILABLE,
                "Facebook API tạm thời không khả dụng",
                503,
                true
            );
        }

        if (content.contains("[FB_429]")) {
            throw new ApiException(
                ErrorCode.FACEBOOK_RATE_LIMIT,
                "Facebook API giới hạn số lượng request",
                429,
                true
            );
        }

        if (content.contains("[FB_400]")) {
            throw new ApiException(
                ErrorCode.FACEBOOK_BAD_REQUEST,
                "Nội dung gửi lên Facebook không hợp lệ",
                400,
                false
            );
        }

        if (content.contains("[FB_401]")) {
            throw new ApiException(
                ErrorCode.FACEBOOK_UNAUTHORIZED,
                "Access token Facebook không hợp lệ hoặc đã hết hạn",
                401,
                false
            );
        }
    }
}