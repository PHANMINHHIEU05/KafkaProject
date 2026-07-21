package com.example.service;

import com.example.entity.OutBox;
import com.example.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxService(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    public OutBox save(OutBox outBox) {
        return outboxEventRepository.save(outBox);
    }

    public List<OutBox> findReadyEvents(int limit) {
        return outboxEventRepository.findReadyEvents(limit);
    }

    public void markPublished(UUID id) {
        outboxEventRepository.markPublished(id);
    }
}
