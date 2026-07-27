Kiem tra nhanh:

```bash
docker --version
docker compose version
java -version
mvn -version
```

## 2. Tao file moi truong

Tao file `.env` o root project:

```env
MINIO_ROOT_USER=socialpostadmin
MINIO_ROOT_PASSWORD=socialpostadmin123
MINIO_ACCESS_KEY=socialpostadmin
MINIO_SECRET_KEY=socialpostadmin123
MINIO_BUCKET=social-post-media

JWT_SECRET=change-this-development-secret-at-least-32-bytes
JWT_EXPIRATION_SECONDS=86400
```

Luu y: `compose.yaml` dang doc `MINIO_ROOT_USER` va `MINIO_ROOT_PASSWORD`. App mac dinh ket noi MinIO bang:

```text
socialpostadmin / socialpostadmin123
```

Nen neu khong muon sua config thi dung dung bo credential tren.

## 3. Chay PostgreSQL, Kafka, MinIO

Chay toan bo service:

```bash
docker compose up -d
```

Kiem tra container:

```bash
docker compose ps
```

Xem log MinIO:

```bash
docker compose logs -f minio
```

Xem log Kafka:

```bash
docker compose logs -f kafka
```

MinIO Console:

```text
http://localhost:9001
```

Dang nhap:

```text
username: socialpostadmin
password: socialpostadmin123
```

PostgreSQL:

```text
host: localhost
port: 5432
database: social_post_db
username: postgres
password: postgres
```

Kafka:

```text
bootstrap server: localhost:9092
```

## 4. Chay Spring Boot app

Tu root project:

```bash
mvn clean test -DskipTests
mvn spring-boot:run
```

Khi app start, Flyway se chay:

- `V1__create_initial_schema.sql`
- `V2__seed_demo_data.sql`

Neu DB da tung chay migration cu va ban muon reset demo tu dau:

```bash
docker compose down -v
docker compose up -d
mvn spring-boot:run
```

## 5. Tai khoan demo

Tat ca user demo co password:

```text
password
```

| Email                          | Role              | Department | Muc dich demo                              |
| ------------------------------ | ----------------- | ---------- | ------------------------------------------ |
| `admin@demo.local`             | `ADMIN`           | Admin      | Xem media toan organization, quan tri demo |
| `creator.marketing@demo.local` | `CONTENT_CREATOR` | Marketing  | Upload video, tao post, publish Kafka      |
| `viewer.marketing@demo.local`  | `VIEWER`          | Marketing  | Chi xem media cua Marketing                |
| `viewer.sales@demo.local`      | `VIEWER`          | Sales      | Chi xem media cua Sales                    |

ID demo quan trong:

| Loai                  |     ID | Mo ta                     |
| --------------------- | -----: | ------------------------- |
| Organization          | `1001` | Demo Social Post          |
| Department            | `1001` | Admin                     |
| Department            | `1002` | Marketing                 |
| Department            | `1003` | Sales                     |
| Facebook channel      | `1001` | Demo Facebook Page        |
| TikTok channel        | `1002` | Demo TikTok Channel       |
| Disabled channel      | `1003` | Khong publish duoc        |
| Marketing ready media | `1001` | Media mau phong Marketing |
| Sales ready media     | `1002` | Media mau phong Sales     |

## 6. Setup Postman environment

Tao Environment trong Postman voi cac bien:

```text
baseUrl=http://localhost:8080
token=
uploadUrl=
downloadUrl=
mediaAssetId=
clientRequestId=demo-post-{{$timestamp}}
facebookChannelId=1001
tiktokChannelId=1002
marketingReadyMediaId=1001
salesReadyMediaId=1002
```

Trong tab Authorization cua cac request API Spring Boot:

```text
Type: Bearer Token
Token: {{token}}
```

Rieng request `PUT {{uploadUrl}}` len MinIO thi khong can Bearer Token.

## 7. Login lay JWT

Request:

```http
POST {{baseUrl}}/api/auth/login
Content-Type: application/json
```

Body:

```json
{
  "email": "creator.marketing@demo.local",
  "password": "password"
}
```

Response se co token. Copy token vao bien Postman:

```text
token
```

Co the login bang cac email khac de demo phan quyen.

## 8. Xem user hien tai

```http
GET {{baseUrl}}/api/users/me
Authorization: Bearer {{token}}
```

Dung request nay de chung minh JWT da duoc doc tu `SecurityContext`.

## 9. Demo upload video MinIO

### 9.1. Tao pre-signed upload URL

Login bang:

```text
creator.marketing@demo.local
```

Request:

```http
POST {{baseUrl}}/api/media-assets/uploads
Authorization: Bearer {{token}}
Content-Type: application/json
```

Body:

```json
{
  "originalFilename": "demo-video.mp4",
  "mediaType": "VIDEO",
  "mimeType": "video/mp4",
  "sizeBytes": 1048576,
  "checksumSha256": null
}
```

Response mau:

```json
{
  "mediaAssetId": 1003,
  "uploadUrl": "http://localhost:9000/...",
  "method": "PUT",
  "headers": {
    "Content-Type": "video/mp4"
  },
  "expiresAt": "..."
}
```

Luu:

```text
mediaAssetId = response.mediaAssetId
uploadUrl = response.uploadUrl
```

### 9.2. PUT file video len MinIO

Request moi trong Postman:

```http
PUT {{uploadUrl}}
Content-Type: video/mp4
```

Body:

```text
Body -> binary -> chon file .mp4 local
```

Khong them Authorization header cho request nay.

Neu thanh cong, MinIO tra status `200 OK`.

### 9.3. Confirm upload

```http
POST {{baseUrl}}/api/media-assets/{{mediaAssetId}}/confirm
Authorization: Bearer {{token}}
```

Sau buoc nay media asset chuyen sang:

```text
READY
```

### 9.4. Tao pre-signed download URL

```http
GET {{baseUrl}}/api/media-assets/{{mediaAssetId}}/download-url
Authorization: Bearer {{token}}
```

Luu:

```text
downloadUrl = response.downloadUrl
```

Mo URL nay tren browser hoac Postman:

```http
GET {{downloadUrl}}
```

## 10. Demo quyen xem media theo phong ban

### Marketing viewer xem media Marketing

Login:

```text
viewer.marketing@demo.local / password
```

Request:

```http
GET {{baseUrl}}/api/media-assets
Authorization: Bearer {{token}}
```

Ket qua mong doi:

- Thay media phong Marketing
- Khong thay media phong Sales

### Sales viewer xem media Sales

Login:

```text
viewer.sales@demo.local / password
```

Request:

```http
GET {{baseUrl}}/api/media-assets
Authorization: Bearer {{token}}
```

Ket qua mong doi:

- Thay media phong Sales
- Khong thay media phong Marketing

### Admin xem toan organization

Login:

```text
admin@demo.local / password
```

Request:

```http
GET {{baseUrl}}/api/media-assets
Authorization: Bearer {{token}}
```

Ket qua mong doi:

- Thay media Marketing
- Thay media Sales

## 11. Demo tao post va Kafka publish

Login bang:

```text
creator.marketing@demo.local / password
```

Dung `mediaAssetId` vua upload va confirm READY. Neu chi muon test nhanh payload, co the dung media mau:

```text
marketingReadyMediaId = 1001
```

Request:

```http
POST {{baseUrl}}/api/post/
Authorization: Bearer {{token}}
Content-Type: application/json
```

Body:

```json
{
  "title": "Demo Kafka publish",
  "content": "Bai demo publish thanh cong qua Facebook va TikTok",
  "socialChannelIds": [1001, 1002],
  "mediaList": [
    {
      "mediaAssetId": 1001,
      "sortOrder": 0
    }
  ],
  "clientRequestId": "demo-post-{{$timestamp}}",
  "scheduledAt": null
}
```

Ket qua mong doi:

- API tao post thanh cong
- Tao `post`
- Tao `post_media`
- Tao `post_target`
- Tao `outbox_event` topic `post-publish-requests`
- `OutboxPublisher` day message vao Kafka
- `FacebookPublishConsumer` va `TikTokPublishConsumer` nhan message
- Fake client tra success
- Tao `publish_attempt`
- Tao outbox result
- `PublishResultConsumer` cap nhat target/post status

Xem log app:

```bash
mvn spring-boot:run
```

Tim cac log:

```text
Gửi Kafka thành công
Facebook publish thành công
TikTok publish thành công
```

## 12. Demo Kafka fail/retry

Dung cac keyword trong content de gia lap loi:

Facebook:

```text
[FB_TIMEOUT]
[FB_503]
[FB_429]
[FB_400]
[FB_401]
```

TikTok:

```text
[TIKTOK_TIMEOUT]
[TIKTOK_503]
[TIKTOK_429]
[TIKTOK_400]
[TIKTOK_401]
```

Vi du body:

```json
{
  "title": "Demo publish failed",
  "content": "Bai demo loi [FB_400]",
  "socialChannelIds": [1001, 1002],
  "mediaList": [
    {
      "mediaAssetId": 1001,
      "sortOrder": 0
    }
  ],
  "clientRequestId": "demo-fail-{{$timestamp}}",
  "scheduledAt": null
}
```

## 13. Query DB nhanh khi can kiem tra

Vao PostgreSQL container:

```bash
docker exec -it social_post_postgres psql -U postgres -d social_post_db
```

Xem users:

```sql
SELECT id, email, status FROM users ORDER BY id;
```

Xem role/permission cua user:

```sql
SELECT
    u.email,
    d.name AS department,
    r.name AS role,
    p.code AS permission
FROM users u
JOIN organization_member om ON om.user_id = u.id
JOIN department d ON d.id = om.department_id
JOIN organization_member_role omr ON omr.organization_member_id = om.id
JOIN role r ON r.id = omr.role_id
JOIN role_permission rp ON rp.role_id = r.id
JOIN permission p ON p.id = rp.permission_id
ORDER BY u.email, r.name, p.code;
```

Xem outbox:

```sql
SELECT id, topic, event_type, status, retry_count, error_code, created_at
FROM outbox_event
ORDER BY id DESC;
```

Xem publish target:

```sql
SELECT id, post_id, social_channel_id, status, external_post_url, error_code, error_message
FROM post_target
ORDER BY id DESC;
```

Xem publish attempt:

```sql
SELECT id, post_target_id, attempt_number, status, http_status_code, error_code, error_message
FROM publish_attempt
ORDER BY id DESC;
```

## 14. Loi thuong gap

### Login 401

Kiem tra:

- App da chay migration V2 chua
- Email dung chua
- Password la `password`
- User co `organization_member` status `ACTIVE`

### Upload tra 403

Kiem tra user co permission:

```text
MEDIA_UPLOAD
```

User nen dung:

```text
creator.marketing@demo.local
```

### PUT uploadUrl len MinIO fail

Kiem tra:

- MinIO container dang chay
- `Content-Type` dung `video/mp4`
- Upload URL chua het han
- Khong gui Bearer Token vao request PUT MinIO

### Download URL tao duoc nhung GET 404

Neu dung media seed san `1001` hoac `1002`, object co the chua ton tai that trong MinIO. De demo download that, hay dung media vua upload qua flow:

```text
initiate upload -> PUT MinIO -> confirm -> download-url
```

### Kafka khong thay log publish

Kiem tra:

```bash
docker compose ps
docker compose logs -f kafka
```

Va trong app log tim:

```text
Không có OutboxEvent nào sẵn sàng để xuất bản
Gửi Kafka thành công
```

### Muon reset demo

Lenh nay xoa het data PostgreSQL va MinIO:

```bash
docker compose down -v
docker compose up -d
mvn spring-boot:run
```
