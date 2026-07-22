package com.example.service;

import com.example.client.PlatformPublishResult;
import com.example.client.TikTokApiClient;
import com.example.entity.PublishAttempt;
import com.example.entity.enums.Platform;
import com.example.event.PublishRequestedEvent;
import com.example.event.PublishResultEvent;
import com.example.event.PublishTargetEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokPublishService {

    private final TikTokApiClient tikTokApiClient;
    private final PublishAttemptService publishAttemptService;
    private final PublishResultOutboxService resultOutboxService;

    public void publish(
        PublishRequestedEvent event,
        PublishTargetEvent target
    ) {
        if (target.platform() != Platform.TIKTOK) {
            throw new IllegalArgumentException(
                "TikTokPublishService chỉ xử lý TIKTOK"
            );
        }

        PublishAttempt attempt =
            publishAttemptService.startAttempt(
                target.postTargetId(),
                Platform.TIKTOK
            );

        try {
            PlatformPublishResult apiResult =
                tikTokApiClient.publish(
                    event,
                    target
                );

            publishAttemptService.markSuccess(
                attempt.getId(),
                apiResult.httpStatusCode()
            );

            PublishResultEvent resultEvent =
                new PublishResultEvent(
                    event.postId(),
                    target.postTargetId(),
                    attempt.getId(),
                    Platform.TIKTOK,
                    true,
                    apiResult.externalPostId(),
                    apiResult.externalPostUrl(),
                    apiResult.httpStatusCode(),
                    null,
                    null,
                    false,
                    Instant.now()
                );

            resultOutboxService.saveResult(resultEvent);

            log.info(
                "TikTok publish thành công: postId={}, targetId={}",
                event.postId(),
                target.postTargetId()
            );

        } catch (Exception exception) {
            String errorMessage =
                getErrorMessage(exception);

            publishAttemptService.markFailure(
                attempt.getId(),
                null,
                "TIKTOK_API_FAILED",
                errorMessage,
                true
            );

            PublishResultEvent resultEvent =
                new PublishResultEvent(
                    event.postId(),
                    target.postTargetId(),
                    attempt.getId(),
                    Platform.TIKTOK,
                    false,
                    null,
                    null,
                    null,
                    "TIKTOK_API_FAILED",
                    errorMessage,
                    true,
                    Instant.now()
                );

            resultOutboxService.saveResult(resultEvent);

            log.error(
                "TikTok publish thất bại: postId={}, targetId={}",
                event.postId(),
                target.postTargetId(),
                exception
            );
        }
    }

    private String getErrorMessage(
        Exception exception
    ) {
        if (exception.getMessage() != null
            && !exception.getMessage().isBlank()) {

            return exception.getMessage();
        }

        return exception
            .getClass()
            .getSimpleName();
    }
}
