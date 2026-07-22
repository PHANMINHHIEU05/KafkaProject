package com.example.service;

import com.example.entity.PostTarget;
import com.example.entity.PublishAttempt;
import com.example.entity.enums.AttemptStatus;
import com.example.entity.enums.Platform;
import com.example.entity.enums.PublishStatus;
import com.example.mapper.PublishAttemptMapper;
import com.example.repository.PostTargetRepository;
import com.example.repository.PublishAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublishAttemptService {

    private final PublishAttemptRepository publishAttemptRepository;
    private final PostTargetRepository postTargetRepository;
    private final PublishAttemptMapper publishAttemptMapper;

    /**
     * Bắt đầu một lần gọi API ngoài.
     */
    @Transactional
    public PublishAttempt startAttempt(
        UUID postTargetId,
        Platform platform
    ) {
        PostTarget target = postTargetRepository
            .findById(postTargetId)
            .orElseThrow(() ->
                new IllegalStateException(
                    "Không tìm thấy PostTarget: "
                        + postTargetId
                )
            );

        /*
         * Consumer nhận lại message đã xử lý thành công
         * thì không gọi API lần nữa.
         */
        if (target.getStatus() == PublishStatus.PUBLISHED) {
            throw new IllegalStateException(
                "PostTarget đã được publish: "
                    + postTargetId
            );
        }

        if (target.getStatus() == PublishStatus.CANCELLED) {
            throw new IllegalStateException(
                "PostTarget đã bị hủy: "
                    + postTargetId
            );
        }

        if (target.getPlatform() != platform) {
            throw new IllegalArgumentException(
                "Platform của target không hợp lệ"
            );
        }

        int lastAttemptNumber =
            publishAttemptRepository.findMaxAttemptNumber(
                postTargetId
            );

        PublishAttempt attempt =
            publishAttemptMapper.toProcessingAttempt(
                target,
                lastAttemptNumber + 1,
                UUID.randomUUID().toString()
            );

        /*
         * Đánh dấu target đang được xử lý.
         */
        target.setStatus(PublishStatus.PROCESSING);
        target.setProcessingStartedAt(Instant.now());

        return publishAttemptRepository.save(attempt);
    }

    /**
     * Cập nhật attempt thành công.
     */
    @Transactional
    public void markSuccess(
        Long attemptId,
        Integer httpStatusCode
    ) {
        PublishAttempt attempt =
            getAttemptOrThrow(attemptId);

        attempt.setStatus(
            AttemptStatus.SUCCESS
        );

        attempt.setHttpStatusCode(httpStatusCode);
        attempt.setErrorCode(null);
        attempt.setErrorMessage(null);
        attempt.setRetryable(false);
        attempt.setFinishedAt(Instant.now());
    }

    /**
     * Cập nhật attempt thất bại.
     */
    @Transactional
    public void markFailure(
        Long attemptId,
        Integer httpStatusCode,
        String errorCode,
        String errorMessage,
        boolean retryable
    ) {
        PublishAttempt attempt =
            getAttemptOrThrow(attemptId);

        attempt.setStatus(
            AttemptStatus.FAILED
        );

        attempt.setHttpStatusCode(httpStatusCode);
        attempt.setErrorCode(errorCode);
        attempt.setErrorMessage(errorMessage);
        attempt.setRetryable(retryable);
        attempt.setFinishedAt(Instant.now());
    }

    private PublishAttempt getAttemptOrThrow(
        Long attemptId
    ) {
        return publishAttemptRepository
            .findById(attemptId)
            .orElseThrow(() ->
                new IllegalStateException(
                    "Không tìm thấy PublishAttempt: "
                        + attemptId
                )
            );
    }
}
