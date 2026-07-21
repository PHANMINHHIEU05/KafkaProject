CREATE INDEX idx_social_account_user_active
    ON social_account(user_id, active);

CREATE INDEX idx_post_user_created_at
    ON post(user_id, created_at DESC);

CREATE INDEX idx_post_target_platform_status
    ON post_target(platform, status);

CREATE INDEX idx_outbox_ready
    ON outbox_event(available_at, created_at)
    WHERE status IN ('NEW', 'RETRY_WAIT');

CREATE INDEX idx_outbox_aggregate
    ON outbox_event(aggregate_type, aggregate_id);
