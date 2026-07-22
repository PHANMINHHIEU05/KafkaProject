package com.example.service;

import com.example.entity.Post;
import com.example.entity.PostTarget;
import com.example.entity.enums.PostStatus;
import com.example.entity.enums.PublishStatus;
import com.example.event.PublishResultEvent;
import com.example.repository.PostRepository;
import com.example.repository.PostTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PublishResultService {

    private final PostTargetRepository postTargetRepository;
    private final PostRepository postRepository;

    @Transactional
    public void handleResult(
        PublishResultEvent event
    ) {
        PostTarget target = postTargetRepository
            .findById(event.postTargetId())
            .orElseThrow(() ->
                new IllegalStateException(
                    "Không tìm thấy PostTarget: "
                        + event.postTargetId()
                )
            );

        /*
         * Idempotency:
         * Event thành công bị gửi lại thì không update lại.
         */
        if (target.getStatus() == PublishStatus.PUBLISHED
            && event.success()) {
            return;
        }

        if (event.success()) {
            target.setStatus(PublishStatus.PUBLISHED);
            target.setExternalPostId(
                event.externalPostId()
            );
            target.setExternalPostUrl(
                event.externalPostUrl()
            );
            target.setErrorCode(null);
            target.setErrorMessage(null);
            target.setPublishedAt(Instant.now());
        } else {
            target.setStatus(PublishStatus.FAILED);
            target.setErrorCode(event.errorCode());
            target.setErrorMessage(
                event.errorMessage()
            );
        }

        Post post = target.getPost();

        recalculatePostStatus(post);
    }

    private void recalculatePostStatus(Post post) {
        long total = post.getTargets().size();

        long publishedCount = post.getTargets()
            .stream()
            .filter(target ->
                target.getStatus()
                    == PublishStatus.PUBLISHED
            )
            .count();

        long failedCount = post.getTargets()
            .stream()
            .filter(target ->
                target.getStatus()
                    == PublishStatus.FAILED
            )
            .count();

        long processingCount = post.getTargets()
            .stream()
            .filter(target ->
                target.getStatus()
                    == PublishStatus.PROCESSING
            )
            .count();

        if (publishedCount == total) {
            post.setStatus(PostStatus.PUBLISHED);
            return;
        }

        if (failedCount == total) {
            post.setStatus(PostStatus.FAILED);
            return;
        }

        /*
         * Nếu enum PostStatus của bạn có PARTIAL_SUCCESS
         * thì nên dùng trạng thái đó ở đây.
         */
        if (publishedCount > 0
            && publishedCount + failedCount == total) {

            post.setStatus(PostStatus.PUBLISHED);
            return;
        }

        if (processingCount > 0) {
            post.setStatus(PostStatus.PROCESSING);
        }
    }
}