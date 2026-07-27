package com.example.service;

import com.example.entity.OutBox;
import com.example.entity.Post;
import com.example.event.PublishResultEvent;
import com.example.mapper.OutboxMapper;
import com.example.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class PublishResultOutboxService {

    private static final String RESULT_TOPIC =
        "post-publish-results";

    private static final String RESULT_EVENT_TYPE =
        "POST_PUBLISH_RESULT";

    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;
    private final OutboxMapper outboxMapper;
    private final PostRepository postRepository;

    public OutBox saveResult(
        PublishResultEvent result
    ) {
        Post post = postRepository.findById(result.postId())
            .orElseThrow(() ->
                new IllegalStateException(
                    "Không tìm thấy Post để tạo result outbox: " + result.postId()
                )
            );

        OutBox event = outboxMapper.toPublishResultOutbox(
            post,
            result,
            objectMapper
        );

        return outboxService.save(event);
    }
}
