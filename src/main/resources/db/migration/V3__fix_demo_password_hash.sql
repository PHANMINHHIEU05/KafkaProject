-- Fix demo user password hashes.
-- Password for every demo user: password

UPDATE users
SET password_hash = '$2a$10$1Y94R6ch.rt8x7Mw.Uv90uPnDNHAAB0HYAaJtVSTT3BLM/6ygjwBK',
    updated_at = CURRENT_TIMESTAMP
WHERE email IN (
    'admin@demo.local',
    'creator.marketing@demo.local',
    'viewer.marketing@demo.local',
    'viewer.sales@demo.local'
);
