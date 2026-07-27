-- ============================================================
-- Demo seed data
-- Password for every demo user: password
-- ============================================================

-- Extra permissions used by the current demo code.
INSERT INTO permission (
    code,
    name,
    description,
    permission_group
)
VALUES
    ('MEDIA_CREATE', 'Tạo media', 'Alias demo cho quyền tạo upload media', 'MEDIA'),
    ('MEDIA_READ_ORGANIZATION', 'Xem media toàn tổ chức', 'Cho phép xem media ở mọi phòng ban trong tổ chức', 'MEDIA')
ON CONFLICT (code) DO NOTHING;


-- ============================================================
-- Organization and departments
-- ============================================================

INSERT INTO organization (
    id,
    name,
    slug,
    description,
    logo_url,
    status
)
VALUES (
    1001,
    'Demo Social Post',
    'demo-social-post',
    'Tổ chức mẫu để demo upload/download media, Kafka publish và RBAC',
    NULL,
    'ACTIVE'
)
ON CONFLICT (slug) DO UPDATE
SET name = EXCLUDED.name,
    description = EXCLUDED.description,
    status = EXCLUDED.status,
    updated_at = CURRENT_TIMESTAMP;

INSERT INTO department (
    id,
    organization_id,
    name,
    description,
    status
)
VALUES
    (1001, 1001, 'Admin', 'Phòng quản trị toàn tổ chức', 'ACTIVE'),
    (1002, 1001, 'Marketing', 'Phòng tạo nội dung và đăng bài', 'ACTIVE'),
    (1003, 1001, 'Sales', 'Phòng bán hàng dùng để demo giới hạn xem media', 'ACTIVE')
ON CONFLICT (organization_id, name) DO UPDATE
SET description = EXCLUDED.description,
    status = EXCLUDED.status,
    updated_at = CURRENT_TIMESTAMP;


-- ============================================================
-- Users
-- ============================================================

INSERT INTO users (
    id,
    first_name,
    last_name,
    phone_number,
    email,
    avatar_url,
    password_hash,
    status
)
VALUES
    (
        1001,
        'Alice',
        'Admin',
        '0900000001',
        'admin@demo.local',
        NULL,
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'ACTIVE'
    ),
    (
        1002,
        'Mark',
        'Creator',
        '0900000002',
        'creator.marketing@demo.local',
        NULL,
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'ACTIVE'
    ),
    (
        1003,
        'Mia',
        'Viewer',
        '0900000003',
        'viewer.marketing@demo.local',
        NULL,
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'ACTIVE'
    ),
    (
        1004,
        'Sam',
        'Sales',
        '0900000004',
        'viewer.sales@demo.local',
        NULL,
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'ACTIVE'
    )
ON CONFLICT (email) DO UPDATE
SET first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    phone_number = EXCLUDED.phone_number,
    password_hash = EXCLUDED.password_hash,
    status = EXCLUDED.status,
    updated_at = CURRENT_TIMESTAMP;


-- ============================================================
-- Organization members
-- ============================================================

INSERT INTO organization_member (
    id,
    organization_id,
    department_id,
    user_id,
    status,
    joined_at
)
VALUES
    (1001, 1001, 1001, 1001, 'ACTIVE', CURRENT_TIMESTAMP),
    (1002, 1001, 1002, 1002, 'ACTIVE', CURRENT_TIMESTAMP),
    (1003, 1001, 1002, 1003, 'ACTIVE', CURRENT_TIMESTAMP),
    (1004, 1001, 1003, 1004, 'ACTIVE', CURRENT_TIMESTAMP)
ON CONFLICT (user_id) DO UPDATE
SET organization_id = EXCLUDED.organization_id,
    department_id = EXCLUDED.department_id,
    status = EXCLUDED.status,
    joined_at = EXCLUDED.joined_at,
    updated_at = CURRENT_TIMESTAMP;


-- ============================================================
-- Roles
-- ============================================================

INSERT INTO role (
    id,
    organization_id,
    department_id,
    name,
    description,
    active
)
VALUES
    (1001, 1001, 1001, 'ADMIN', 'Toàn quyền demo trong tổ chức', TRUE),
    (1002, 1001, 1002, 'CONTENT_CREATOR', 'Tạo media và tạo bài trong phòng Marketing', TRUE),
    (1003, 1001, 1002, 'VIEWER', 'Chỉ xem media/bài trong phòng Marketing', TRUE),
    (1004, 1001, 1003, 'VIEWER', 'Chỉ xem media/bài trong phòng Sales', TRUE)
ON CONFLICT (organization_id, department_id, name) DO UPDATE
SET description = EXCLUDED.description,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP;


-- ADMIN role: enough permissions for the whole demo.
INSERT INTO role_permission (
    role_id,
    permission_id
)
SELECT 1001, p.id
FROM permission p
WHERE p.code IN (
    'POST_READ',
    'POST_CREATE',
    'POST_UPDATE',
    'POST_DELETE',
    'POST_PUBLISH',
    'POST_CANCEL',
    'MEDIA_READ',
    'MEDIA_UPLOAD',
    'MEDIA_CREATE',
    'MEDIA_DELETE',
    'MEDIA_READ_ORGANIZATION',
    'SOCIAL_ACCOUNT_READ',
    'SOCIAL_ACCOUNT_CONNECT',
    'SOCIAL_ACCOUNT_UPDATE',
    'SOCIAL_ACCOUNT_DISCONNECT',
    'SOCIAL_CHANNEL_READ',
    'SOCIAL_CHANNEL_SYNC',
    'SOCIAL_CHANNEL_UPDATE',
    'ORGANIZATION_READ',
    'ORGANIZATION_MEMBER_READ',
    'DEPARTMENT_READ',
    'ROLE_READ',
    'ROLE_ASSIGN',
    'PUBLISH_ATTEMPT_READ',
    'OUTBOX_EVENT_READ',
    'AUDIT_LOG_READ'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Marketing creator: can upload media, create posts and publish to seeded channels.
INSERT INTO role_permission (
    role_id,
    permission_id
)
SELECT 1002, p.id
FROM permission p
WHERE p.code IN (
    'POST_READ',
    'POST_CREATE',
    'POST_UPDATE',
    'POST_PUBLISH',
    'POST_CANCEL',
    'MEDIA_READ',
    'MEDIA_UPLOAD',
    'MEDIA_CREATE',
    'MEDIA_DELETE',
    'SOCIAL_ACCOUNT_READ',
    'SOCIAL_CHANNEL_READ'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Marketing viewer: can only read visible media/posts in own department.
INSERT INTO role_permission (
    role_id,
    permission_id
)
SELECT 1003, p.id
FROM permission p
WHERE p.code IN (
    'POST_READ',
    'MEDIA_READ',
    'SOCIAL_ACCOUNT_READ',
    'SOCIAL_CHANNEL_READ'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Sales viewer: same permissions as viewer, but different department.
INSERT INTO role_permission (
    role_id,
    permission_id
)
SELECT 1004, p.id
FROM permission p
WHERE p.code IN (
    'POST_READ',
    'MEDIA_READ',
    'SOCIAL_ACCOUNT_READ',
    'SOCIAL_CHANNEL_READ'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;


-- ============================================================
-- Member-role assignment
-- ============================================================

INSERT INTO organization_member_role (
    organization_member_id,
    role_id
)
VALUES
    (1001, 1001),
    (1002, 1002),
    (1003, 1003),
    (1004, 1004)
ON CONFLICT (organization_member_id, role_id) DO NOTHING;


-- ============================================================
-- Social accounts and publish channels
-- ============================================================

INSERT INTO social_account (
    id,
    organization_id,
    user_id,
    platform,
    external_account_id,
    account_name,
    avatar_url,
    access_token_encrypted,
    refresh_token_encrypted,
    token_expires_at,
    active,
    connection_status,
    connected_at,
    last_synced_at
)
VALUES
    (
        1001,
        1001,
        1001,
        'FACEBOOK',
        'fb-demo-business-001',
        'Demo Facebook Business',
        NULL,
        'demo-facebook-access-token',
        'demo-facebook-refresh-token',
        CURRENT_TIMESTAMP + INTERVAL '90 days',
        TRUE,
        'CONNECTED',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        1002,
        1001,
        1001,
        'TIKTOK',
        'tt-demo-business-001',
        'Demo TikTok Business',
        NULL,
        'demo-tiktok-access-token',
        'demo-tiktok-refresh-token',
        CURRENT_TIMESTAMP + INTERVAL '90 days',
        TRUE,
        'CONNECTED',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (organization_id, platform, external_account_id) DO UPDATE
SET account_name = EXCLUDED.account_name,
    access_token_encrypted = EXCLUDED.access_token_encrypted,
    refresh_token_encrypted = EXCLUDED.refresh_token_encrypted,
    token_expires_at = EXCLUDED.token_expires_at,
    active = EXCLUDED.active,
    connection_status = EXCLUDED.connection_status,
    last_synced_at = EXCLUDED.last_synced_at,
    updated_at = CURRENT_TIMESTAMP;

INSERT INTO social_channel (
    id,
    social_account_id,
    external_channel_id,
    channel_type,
    channel_name,
    avatar_url,
    can_publish,
    status,
    channel_access_token_encrypted,
    channel_token_expires_at,
    metadata,
    last_synced_at
)
VALUES
    (
        1001,
        1001,
        'fb-page-demo-001',
        'PAGE',
        'Demo Facebook Page',
        NULL,
        TRUE,
        'ACTIVE',
        'demo-facebook-page-token',
        CURRENT_TIMESTAMP + INTERVAL '90 days',
        '{"demo": true, "platform": "FACEBOOK"}'::jsonb,
        CURRENT_TIMESTAMP
    ),
    (
        1002,
        1002,
        'tt-channel-demo-001',
        'BUSINESS_ACCOUNT',
        'Demo TikTok Channel',
        NULL,
        TRUE,
        'ACTIVE',
        'demo-tiktok-channel-token',
        CURRENT_TIMESTAMP + INTERVAL '90 days',
        '{"demo": true, "platform": "TIKTOK"}'::jsonb,
        CURRENT_TIMESTAMP
    ),
    (
        1003,
        1001,
        'fb-page-disabled-001',
        'PAGE',
        'Demo Disabled Facebook Page',
        NULL,
        FALSE,
        'ACTIVE',
        'demo-disabled-token',
        CURRENT_TIMESTAMP + INTERVAL '90 days',
        '{"demo": true, "canPublish": false}'::jsonb,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (social_account_id, external_channel_id, channel_type) DO UPDATE
SET channel_name = EXCLUDED.channel_name,
    can_publish = EXCLUDED.can_publish,
    status = EXCLUDED.status,
    channel_access_token_encrypted = EXCLUDED.channel_access_token_encrypted,
    channel_token_expires_at = EXCLUDED.channel_token_expires_at,
    metadata = EXCLUDED.metadata,
    last_synced_at = EXCLUDED.last_synced_at,
    updated_at = CURRENT_TIMESTAMP;


-- ============================================================
-- Ready media assets for visibility demo
-- These objects may not physically exist in MinIO; upload flow should
-- still be demoed with POST /api/media-assets/uploads + PUT + confirm.
-- ============================================================

INSERT INTO media_asset (
    id,
    organization_id,
    department_id,
    user_id,
    bucket_name,
    object_key,
    original_filename,
    media_type,
    mime_type,
    size_bytes,
    etag,
    object_version_id,
    checksum_sha256,
    upload_status,
    confirmed_at
)
VALUES
    (
        1001,
        1001,
        1002,
        1002,
        'social-post-media',
        'demo/marketing/ready-video.mp4',
        'marketing-demo-video.mp4',
        'VIDEO',
        'video/mp4',
        5242880,
        'demo-marketing-etag',
        NULL,
        NULL,
        'READY',
        CURRENT_TIMESTAMP
    ),
    (
        1002,
        1001,
        1003,
        1004,
        'social-post-media',
        'demo/sales/ready-video.mp4',
        'sales-demo-video.mp4',
        'VIDEO',
        'video/mp4',
        7340032,
        'demo-sales-etag',
        NULL,
        NULL,
        'READY',
        CURRENT_TIMESTAMP
    )
ON CONFLICT (bucket_name, object_key) DO UPDATE
SET original_filename = EXCLUDED.original_filename,
    media_type = EXCLUDED.media_type,
    mime_type = EXCLUDED.mime_type,
    size_bytes = EXCLUDED.size_bytes,
    etag = EXCLUDED.etag,
    upload_status = EXCLUDED.upload_status,
    confirmed_at = EXCLUDED.confirmed_at,
    updated_at = CURRENT_TIMESTAMP;


-- ============================================================
-- Keep identity sequences above explicit demo IDs.
-- ============================================================

SELECT setval(pg_get_serial_sequence('users', 'id'), GREATEST((SELECT MAX(id) FROM users), 1), TRUE);
SELECT setval(pg_get_serial_sequence('organization', 'id'), GREATEST((SELECT MAX(id) FROM organization), 1), TRUE);
SELECT setval(pg_get_serial_sequence('department', 'id'), GREATEST((SELECT MAX(id) FROM department), 1), TRUE);
SELECT setval(pg_get_serial_sequence('organization_member', 'id'), GREATEST((SELECT MAX(id) FROM organization_member), 1), TRUE);
SELECT setval(pg_get_serial_sequence('role', 'id'), GREATEST((SELECT MAX(id) FROM role), 1), TRUE);
SELECT setval(pg_get_serial_sequence('permission', 'id'), GREATEST((SELECT MAX(id) FROM permission), 1), TRUE);
SELECT setval(pg_get_serial_sequence('social_account', 'id'), GREATEST((SELECT MAX(id) FROM social_account), 1), TRUE);
SELECT setval(pg_get_serial_sequence('social_channel', 'id'), GREATEST((SELECT MAX(id) FROM social_channel), 1), TRUE);
SELECT setval(pg_get_serial_sequence('media_asset', 'id'), GREATEST((SELECT MAX(id) FROM media_asset), 1), TRUE);
