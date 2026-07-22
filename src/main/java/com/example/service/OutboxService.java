package com.example.service;

import com.example.entity.OutBox;
import com.example.entity.enums.OutboxStatus;
import com.example.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public OutBox save(OutBox outBox) {
        return outboxEventRepository.save(outBox);
    }

    @Transactional
    public List<OutBox> findReadyEvents(int limit) {
        return outboxEventRepository.findReadyEvents(limit);
    }

    @Transactional
    public void markProcessing(UUID id) {
        int updatedRows = outboxEventRepository.markProcessing(
            id,
            OutboxStatus.NEW,
            OutboxStatus.RETRY_WAIT,
            OutboxStatus.PROCESSING
        );

        if (updatedRows == 0) {
            throw new IllegalStateException(
                "Không thể chuyển OutboxEvent sang PROCESSING: " + id
            );
        }
    }

    @Transactional
    public List<OutBox> claimReadyEvents(int limit) {
        List<OutBox> events =
            outboxEventRepository.findReadyEvents(limit);

        for (OutBox event : events) {
            event.setStatus(OutboxStatus.PROCESSING);
        }
        return events;
    }

    @Transactional
    public void markPublished(UUID id) {
        int updatedRows = outboxEventRepository.markPublished(
            id,
            OutboxStatus.PUBLISHED,
            Instant.now()
        );

        if (updatedRows == 0) {
            throw new IllegalStateException(
                "Không thể đánh dấu OutboxEvent PUBLISHED: " + id
            );
        }
    }

    @Transactional
    public void markFailed(
        UUID id,
        String errorCode,
        String errorMessage
    ) {
        OutBox event = outboxEventRepository.findById(id)
            .orElseThrow(() ->
                new IllegalStateException(
                    "Không tìm thấy OutboxEvent: " + id
                )
            );

        int currentRetryCount =
            event.getRetryCount() == null
                ? 0
                : event.getRetryCount();

        int maxRetry =
            event.getMaxRetry() == null
                ? 10
                : event.getMaxRetry();

        int nextRetryCount = currentRetryCount + 1;

        event.setRetryCount(nextRetryCount);
        event.setErrorCode(errorCode);
        event.setErrorMessage(errorMessage);

        if (nextRetryCount >= maxRetry) {
            event.setStatus(OutboxStatus.DEAD);
        } else {
            event.setStatus(OutboxStatus.RETRY_WAIT);
            event.setAvailableAt(
                calculateNextRetry(nextRetryCount)
            );
        }
    }

    private Instant calculateNextRetry(int retryCount) {
        long delaySeconds = Math.min(
            60L * retryCount,
            900L
        );

        return Instant.now().plusSeconds(delaySeconds);
    }
    private OutBox findByIdOrThrow(UUID eventId) {
        return outboxEventRepository.findById(eventId)
            .orElseThrow(() ->
                new IllegalStateException(
                    "Không tìm thấy OutboxEvent: "
                        + eventId
                )
            );
    }
}