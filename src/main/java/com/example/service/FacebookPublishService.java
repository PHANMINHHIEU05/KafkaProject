package com.example.service;

import com.example.client.FacebookApiClient;
import com.example.client.PlatformPublishResult;
import com.example.entity.PublishAttempt;
import com.example.entity.enums.Platform;
import com.example.event.PublishRequestedEvent;
import com.example.event.PublishResultEvent;
import com.example.event.PublishTargetEvent;
import com.example.mapper.PublishEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacebookPublishService {

    private final FacebookApiClient facebookApiClient;
    private final PublishAttemptService publishAttemptService;
    private final PublishResultOutboxService resultOutboxService;
    private final PublishEventMapper publishEventMapper;

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
                publishEventMapper.toSuccessResultEvent(
                    event,
                    target,
                    attempt,
                    Platform.FACEBOOK,
                    apiResult
                );

            resultOutboxService.saveResult(resultEvent);

            log.info(
                "Facebook publish thành công: postId={}, targetId={}",
                event.postId(),
                target.postTargetId()
            );

        } catch (Exception exception) {
            String errorMessage = getErrorMessage(exception);

            publishAttemptService.markFailure(
                attempt.getId(),
                null,
                "FACEBOOK_API_FAILED",
                errorMessage,
                true
            );

            PublishResultEvent resultEvent =
                publishEventMapper.toFailureResultEvent(
                    event,
                    target,
                    attempt,
                    Platform.FACEBOOK,
                    "FACEBOOK_API_FAILED",
                    errorMessage,
                    true
                );

            resultOutboxService.saveResult(resultEvent);

            log.error("Facebook publish thất bại: postId={}, targetId={}", event.postId(), target.postTargetId(), exception);
        }
    }

    private String getErrorMessage(
        Exception exception
    ) {
        if (exception.getMessage() != null && !exception.getMessage().isBlank()) {
            return exception.getMessage();
        }

        return exception.getClass().getSimpleName();
    }
}
