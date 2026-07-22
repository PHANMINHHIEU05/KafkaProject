# Huong Dan Test API Bang Postman

Tai lieu nay dung cho project auto dang bai len Facebook/TikTok bang Spring Boot, PostgreSQL, Outbox va Kafka.

## 1. Chuan Bi Moi Truong

Chay PostgreSQL va Kafka:

```bash
docker compose up -d
```

Neu ban dang dung container Kafka cu:

```bash
docker start social_media_kafka
```

Chay ung dung de Flyway tao bang database:

```bash
mvn spring-boot:run
```

Cho toi khi log co dong tuong tu:

```text
Successfully applied ... migration
Initialized JPA EntityManagerFactory
Tomcat started on port 8080
```

Base URL trong Postman:

```text
http://localhost:8080
```

Khong them `/api/v1` vao `baseUrl`. Project hien tai khong cau hinh context path `/api/v1`, nen neu goi:

```text
http://localhost:8080/api/v1/api/social-account
```

thi se sai route. URL dung la:

```text
http://localhost:8080/api/social-account
http://localhost:8080/api/post/
```

## 2. Tao User Test Trong Database

Hien tai project chua co API tao user, nen can insert user truc tiep vao PostgreSQL truoc khi test.

Luu y: phai chay app it nhat mot lan truoc de Flyway tao bang. Neu insert ma gap loi `relation "users" does not exist` thi nghia la chua migrate database.

Mo terminal:

```bash
docker exec -it social_post_postgres psql -U postgres -d social_post_db
```

Chay SQL:

```sql
INSERT INTO users (
    first_name,
    last_name,
    phone_number,
    email,
    password_hash,
    status
)
VALUES (
    'Test',
    'User',
    '0900000001',
    'test@example.com',
    'password-hash-test',
    'ACTIVE'
)
RETURNING id;
```

id = 4253f588-8a26-4e39-ac06-d3569905bd81

```text
baseUrl = http://localhost:8080
userId = UUID_USER_VUA_COPY
facebookAccountId =
tiktokAccountId =
postId =
```

Tat ca request ben duoi them header:

```text
Content-Type: application/json
x-user-id: {{userId}}
```

## 3. Tao Social Account Facebook

Method:

```text
POST {{baseUrl}}/api/social-account
```

Body:

```json
{
  "platform": "FACEBOOK",
  "externalAccountId": "fb-page-001",
  "accountName": "Facebook Page Test"
}
```

Ket qua mong doi: HTTP `200 OK`.

Copy field `id` trong response vao Postman variable:

```text
facebookAccountId = id cua response
```

Response mau:

```json
{
  "id": "uuid-social-account",
  "userId": "uuid-user",
  "platform": "FACEBOOK",
  "externalAccountId": "fb-page-001",
  "accountName": "Facebook Page Test",
  "active": true,
  "connectionStatus": "CONNECTED",
  "connectedAt": "2026-07-22T01:00:00Z",
  "createdAt": "2026-07-22T01:00:00Z",
  "updatedAt": "2026-07-22T01:00:00Z"
}
```

## 4. Tao Social Account TikTok

Method:

```text
POST {{baseUrl}}/api/social-account
```

Body:

```json
{
  "platform": "TIKTOK",
  "externalAccountId": "tiktok-account-001",
  "accountName": "TikTok Account Test"
}
```

Copy field `id` trong response vao:

```text
tiktokAccountId = id cua response
```

## 5. Lay Danh Sach Social Account

Method:

```text
GET {{baseUrl}}/api/social-account
```

Header:

```text
x-user-id: {{userId}}
```

Ket qua mong doi: tra ve danh sach account active cua user.

## 6. Tao Bai Dang Dang Ngay

Method:

```text
POST {{baseUrl}}/api/post/
```

Body:

```json
{
  "title": "Bai test dang ngay",
  "content": "Noi dung bai viet test dang tu dong qua Kafka",
  "socialAccountIds": ["{{facebookAccountId}}", "{{tiktokAccountId}}"],
  "mediaList": [
    {
      "mediaType": "IMAGE",
      "mediaUrl": "https://example.com/image-1.jpg",
      "mimeType": "image/jpeg",
      "thumbnailUrl": "https://example.com/thumb-1.jpg",
      "sortOrder": 0
    }
  ],
  "clientRequestId": "postman-test-now-001",
  "scheduledAt": null
}
```

Quan trong: `null` phai la JSON null, khong phai chuoi. Dung:

```json
"scheduledAt": null
```

Khong dung:

```json
"scheduledAt": "null"
```

Ban cung co the bo han field `scheduledAt` neu muon dang ngay.

Ket qua mong doi: HTTP `202 Accepted`.

Copy field `id` trong response vao:

```text
postId = id cua response
```

Trang thai ban dau:

```text
QUEUED
```

Sau khi tao post, he thong se tao record trong `outbox_event`. Scheduler `OutboxPublisher` se doc outbox va day message sang Kafka topic `post-publish-requests`.

## 7. Tao Bai Dang Hen Gio

Method:

```text
POST {{baseUrl}}/api/post/
```

Body:

```json
{
  "title": "Bai test hen gio",
  "content": "Noi dung bai viet se duoc publish sau",
  "socialAccountIds": ["{{facebookAccountId}}"],
  "mediaList": [
    {
      "mediaType": "VIDEO",
      "mediaUrl": "https://example.com/video-1.mp4",
      "mimeType": "video/mp4",
      "thumbnailUrl": "https://example.com/video-thumb-1.jpg",
      "sortOrder": 0
    }
  ],
  "clientRequestId": "postman-test-scheduled-001",
  "scheduledAt": "2026-12-31T10:00:00Z"
}
```

Ket qua mong doi:

```text
202 Accepted
status = SCHEDULED
```

Luu y: `scheduledAt` phai lon hon thoi diem hien tai, neu khong API tra ve loi `INVALID_REQUEST`.

## 8. Lay Chi Tiet Bai Dang

Method:

```text
GET {{baseUrl}}/api/post/{{postId}}
```

Header:

```text
x-user-id: {{userId}}
```

Ket qua mong doi: tra ve chi tiet post, media va targets.

## 9. Lay Danh Sach Bai Dang

Lay tat ca:

```text
GET {{baseUrl}}/api/post/?page=0&size=10
```

Loc theo status:

```text
GET {{baseUrl}}/api/post/?status=QUEUED&page=0&size=10
```

Gia tri `status` hop le:

```text
DRAFT, SCHEDULED, QUEUED, PROCESSING, PUBLISHED, FAILED, CANCELLED
```

## 10. Huy Bai Dang

Method:

```text
PATCH {{baseUrl}}/api/post/{{postId}}/cancel
```

Header:

```text
x-user-id: {{userId}}
```

Ket qua mong doi:

```text
200 OK
status = CANCELLED
```

Luu y: bai dang da `PUBLISHED`, `FAILED`, `CANCELLED`, hoac target dang `PROCESSING` se khong huy duoc.

## 11. Ngat Ket Noi Social Account

Method:

```text
PATCH {{baseUrl}}/api/social-account?accountIds={{facebookAccountId}}
```

Header:

```text
x-user-id: {{userId}}
```

Ket qua mong doi:

```text
200 OK
```

Hien tai code dang xoa record social account khoi DB.

## 12. Cac Loi Thuong Gap

### Loi `USER_NOT_FOUND`

Nguyen nhan: header `x-user-id` khong co, sai UUID, hoac user chua ton tai trong bang `users`.

Xu ly: insert user test theo muc 2 va gan dung bien `userId`.

### Loi duplicate social account

Neu tao lai cung `platform` va `externalAccountId`, API se bao trung.

Doi `externalAccountId`, vi du:

```json
{
  "platform": "FACEBOOK",
  "externalAccountId": "fb-page-002",
  "accountName": "Facebook Page Test 2"
}
```

### Loi duplicate clientRequestId

Neu tao lai post voi cung `clientRequestId`, API se bao:

```text
DUPLICATE_CLIENT_REQUEST
```

Doi `clientRequestId`, vi du:

```text
postman-test-now-002
```

### Loi `INVALID_SOCIAL_ACCOUNT`

Nguyen nhan:

- `socialAccountIds` rong.
- ID account sai.
- Account khong thuoc user hien tai.
- Account da bi xoa/ngat ket noi.

### Kafka bao `UNKNOWN_TOPIC_OR_PARTITION`

Neu Kafka chua co topic, can tao topic:

```bash
docker exec -it social_media_kafka kafka-topics --bootstrap-server localhost:9092 --create --topic post-publish-requests --partitions 1 --replication-factor 1
docker exec -it social_media_kafka kafka-topics --bootstrap-server localhost:9092 --create --topic post-publish-results --partitions 1 --replication-factor 1
```

Kiem tra topic:

```bash
docker exec -it social_media_kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### Scheduler bao `Khong co OutboxEvent nao san sang`

Nghia la khong co ban ghi `outbox_event` nao thoa dieu kien:

```sql
status IN ('NEW', 'RETRY_WAIT')
AND available_at <= CURRENT_TIMESTAMP
```

Kiem tra outbox cua bai dang:

```sql
SELECT
    p.id AS post_id,
    p.status AS post_status,
    pt.id AS target_id,
    pt.status AS target_status,
    o.id AS outbox_id,
    o.status AS outbox_status,
    o.available_at,
    o.error_code,
    o.error_message
FROM post p
LEFT JOIN post_target pt ON pt.post_id = p.id
LEFT JOIN outbox_event o ON o.aggregate_id = p.id
WHERE p.id = 'POST_ID_CUA_BAN'
ORDER BY o.created_at DESC;
```

Neu outbox dang `RETRY_WAIT` hoac `PROCESSING` do lan truoc gui Kafka fail, reset lai de test ngay:

```sql
UPDATE outbox_event
SET status = 'NEW',
    available_at = CURRENT_TIMESTAMP,
    error_code = NULL,
    error_message = NULL
WHERE aggregate_id = 'POST_ID_CUA_BAN'
  AND event_type = 'POST_PUBLISH_REQUESTED';
```

Neu outbox co `available_at` trong tuong lai, do bai dang la bai hen gio. Muon test ngay thi tao post voi:

```json
"scheduledAt": null
```

## 13. Thu Tu Test Nen Chay

1. `docker compose up -d`
2. `mvn spring-boot:run` va doi Flyway tao bang xong.
3. Insert user test vao DB.
4. Tao Facebook social account.
5. Tao TikTok social account.
6. GET danh sach social account.
7. POST tao bai dang ngay.
8. GET chi tiet bai dang.
9. GET danh sach bai dang.
10. PATCH huy bai dang voi mot bai chua publish.
