package com.example.client;

import com.example.event.PublishRequestedEvent;
import com.example.event.PublishTargetEvent;
import com.example.exception.ApiException;
import com.example.exception.ErrorCode;

import org.springframework.stereotype.Component;

    import java.util.UUID;

@Component
public class TikTokApiClient {

    public PlatformPublishResult publish(
        PublishRequestedEvent event,
        PublishTargetEvent target
    ) {
        simulateError(event);

        String externalPostId =
            "tiktok-" + UUID.randomUUID();

        String externalPostUrl =
            "https://tiktok.com/posts/" + externalPostId;

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

    private void simulateError(
        PublishRequestedEvent event
    ) {
        String content = event.content();

        if (content == null) {
            return;
        }

        if (content.contains("[TIKTOK_TIMEOUT]")) {
            throw new ApiException(
                ErrorCode.TIKTOK_TIMEOUT,
                "TikTok API bị timeout",
                504,
                true
            );
        }

        if (content.contains("[TIKTOK_503]")) {
            throw new ApiException(
                ErrorCode.TIKTOK_SERVICE_UNAVAILABLE,
                "TikTok API tạm thời không khả dụng",
                503,
                true
            );
        }

        if (content.contains("[TIKTOK_429]")) {
            throw new ApiException(
                ErrorCode.TIKTOK_RATE_LIMIT,
                "TikTok API giới hạn số lượng request",
                429,
                true
            );
        }

        if (content.contains("[TIKTOK_400]")) {
            throw new ApiException(
                ErrorCode.TIKTOK_BAD_REQUEST,
                "Nội dung gửi lên TikTok không hợp lệ",
                400,
                false
            );
        }

        if (content.contains("[TIKTOK_401]")) {
            throw new ApiException(
                ErrorCode.TIKTOK_UNAUTHORIZED,
                "Access token TikTok không hợp lệ hoặc đã hết hạn",
                401,
                false
            );
        }
    }
}