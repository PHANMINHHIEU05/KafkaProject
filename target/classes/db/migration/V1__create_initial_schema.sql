CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS citext;
 

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,

    phone_number VARCHAR(20),
    email CITEXT NOT NULL,
    avatar_url TEXT,

    password_hash VARCHAR(255) NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_users_email
        UNIQUE (email),

    CONSTRAINT uk_users_phone_number
        UNIQUE (phone_number),

    CONSTRAINT ck_users_status
        CHECK (
            status IN (
                'ACTIVE',
                'INACTIVE',
                'DISABLED'
            )
        )
);


CREATE TABLE app_role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    CONSTRAINT uk_app_role_name
        UNIQUE (name)
);



CREATE TABLE user_role (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    CONSTRAINT pk_user_role
        PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_role_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_role_role
        FOREIGN KEY (role_id)
        REFERENCES app_role(id)
        ON DELETE CASCADE
);



CREATE TABLE social_account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    platform VARCHAR(20) NOT NULL,
    external_account_id VARCHAR(255) NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    connection_status VARCHAR(30) NOT NULL DEFAULT 'CONNECTED',
    connected_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_social_account_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT ck_social_account_platform
        CHECK (
            platform IN (
                'FACEBOOK',
                'TIKTOK'
            )
        ),
    CONSTRAINT ck_social_account_connection_status
        CHECK (
            connection_status IN (
                'CONNECTED',
                'DISCONNECTED',
                'EXPIRED',
                'ERROR'
            )
        ),

    CONSTRAINT uk_social_account_external
        UNIQUE (platform, external_account_id),
    -- Phục vụ composite foreign key từ post_target.
    CONSTRAINT uk_social_account_id_platform
        UNIQUE (id, platform)
);



CREATE TABLE post (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,

    title VARCHAR(500),
    content TEXT NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    client_request_id VARCHAR(255),

    scheduled_at TIMESTAMPTZ,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE RESTRICT,

    CONSTRAINT ck_post_status
        CHECK (
            status IN (
                'DRAFT',
                'SCHEDULED',
                'QUEUED',
                'PROCESSING',
                'PUBLISHED',
                'FAILED',
                'CANCELLED'
            )
        ),

    CONSTRAINT uk_post_client_request
        UNIQUE (user_id, client_request_id)
);



CREATE TABLE post_media (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID NOT NULL,
    media_type VARCHAR(20) NOT NULL,
    media_url TEXT NOT NULL,
    mime_type VARCHAR(100),
    thumbnail_url TEXT,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_post_media_post
        FOREIGN KEY (post_id)
        REFERENCES post(id)
        ON DELETE CASCADE,
    CONSTRAINT ck_post_media_type
        CHECK (
            media_type IN (
                'IMAGE',
                'VIDEO'
            )
        ),

    CONSTRAINT ck_post_media_sort_order
        CHECK (sort_order >= 0),

    CONSTRAINT uk_post_media_sort_order
        UNIQUE (post_id, sort_order)
);


CREATE TABLE post_target (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    post_id UUID NOT NULL,
    social_account_id UUID NOT NULL,

    platform VARCHAR(20) NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    idempotency_key VARCHAR(255) NOT NULL,

    external_post_id VARCHAR(255),
    external_post_url TEXT,

    error_code VARCHAR(100),
    error_message TEXT,

    processing_started_at TIMESTAMPTZ,
    published_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post_target_post
        FOREIGN KEY (post_id)
        REFERENCES post(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_post_target_social_account
        FOREIGN KEY (social_account_id, platform)
        REFERENCES social_account(id, platform)
        ON DELETE RESTRICT,

    CONSTRAINT ck_post_target_platform
        CHECK (
            platform IN (
                'FACEBOOK',
                'TIKTOK'
            )
        ),

    CONSTRAINT ck_post_target_status
        CHECK (
            status IN (
                'PENDING',
                'PROCESSING',
                'PUBLISHED',
                'FAILED',
                'CANCELLED',
                'SKIPPED'
            )
        ),

    CONSTRAINT uk_post_target_idempotency_key
        UNIQUE (idempotency_key),
    CONSTRAINT uk_post_target_account
        UNIQUE (post_id, social_account_id)
);



CREATE TABLE publish_attempt (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,

    post_target_id UUID NOT NULL,

    attempt_number INTEGER NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'PROCESSING',

    request_id VARCHAR(255) NOT NULL,

    http_status_code INTEGER,

    retryable BOOLEAN NOT NULL DEFAULT FALSE,

    error_code VARCHAR(255),
    error_message TEXT,

    started_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMPTZ,

    CONSTRAINT fk_publish_attempt_target
        FOREIGN KEY (post_target_id)
        REFERENCES post_target(id)
        ON DELETE CASCADE,

    CONSTRAINT ck_publish_attempt_number
        CHECK (attempt_number > 0),

    CONSTRAINT ck_publish_attempt_status
        CHECK (
            status IN (
                'PROCESSING',
                'SUCCESS',
                'FAILED',
                'CANCELLED'
            )
        ),

    CONSTRAINT ck_publish_attempt_http_status
        CHECK (
            http_status_code IS NULL
            OR http_status_code BETWEEN 100 AND 599
        ),

    CONSTRAINT ck_publish_attempt_finished_at
        CHECK (
            finished_at IS NULL
            OR finished_at >= started_at
        ),

    CONSTRAINT uk_publish_attempt_number
        UNIQUE (post_target_id, attempt_number),

    CONSTRAINT uk_publish_attempt_request_id
        UNIQUE (request_id)
);


CREATE TABLE outbox_event (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_id UUID NOT NULL,
    aggregate_type VARCHAR(100) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    event_key VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',

    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retry INTEGER NOT NULL DEFAULT 10,

    available_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMPTZ,
    error_code VARCHAR(100),
    error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_outbox_status
        CHECK (
            status IN (
                'NEW',
                'PROCESSING',
                'RETRY_WAIT',
                'PUBLISHED',
                'DEAD'
            )
        ),

    CONSTRAINT ck_outbox_retry_count
        CHECK (retry_count >= 0),
    CONSTRAINT ck_outbox_max_retry
        CHECK (max_retry >= 0),
    CONSTRAINT ck_outbox_retry_limit
        CHECK (retry_count <= max_retry)
);


CREATE TABLE audit_log (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    actor_user_id UUID,
    action VARCHAR(255) NOT NULL,
    target_type VARCHAR(255) NOT NULL,
    target_id UUID,
    old_value JSONB,
    new_value JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_log_actor FOREIGN KEY (actor_user_id) REFERENCES users(id) ON DELETE SET NULL
);
