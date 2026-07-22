package com.example.service;

import com.example.client.FacebookApiClient;
import com.example.client.PlatformPublishResult;
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
public class FacebookPublishService {

    private final FacebookApiClient facebookApiClient;
    private final PublishAttemptService publishAttemptService;
    private final PublishResultOutboxService resultOutboxService;

    public void publish(
        PublishRequestedEvent event,
        PublishTargetEvent target
    ) {
        if (target.platform() != Platform.FACEBOOK) {
            throw new IllegalArgumentException(
                "FacebookPublishService chỉ xử lý FACEBOOK"
            );
        }

        PublishAttempt attempt =
            publishAttemptService.startAttempt(
                target.postTargetId(),
                Platform.FACEBOOK
            );

        try {
            PlatformPublishResult apiResult =
                facebookApiClient.publish(
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
                    Platform.FACEBOOK,
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
                "Facebook publish thành công: postId={}, targetId={}",
                event.postId(),
                target.postTargetId()
            );

        } catch (Exception exception) {
            String errorMessage =
                getErrorMessage(exception);

            publishAttemptService.markFailure(
                attempt.getId(),
                null,
                "FACEBOOK_API_FAILED",
                errorMessage,
                true
            );

            PublishResultEvent resultEvent =
                new PublishResultEvent(
                    event.postId(),
                    target.postTargetId(),
                    attempt.getId(),
                    Platform.FACEBOOK,
                    false,
                    null,
                    null,
                    null,
                    "FACEBOOK_API_FAILED",
                    errorMessage,
                    true,
                    Instant.now()
                );

            resultOutboxService.saveResult(resultEvent);

            log.error(
                "Facebook publish thất bại: postId={}, targetId={}",
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
