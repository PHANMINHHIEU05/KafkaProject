package com.example.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    USER_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "USER_NOT_FOUND",
        "Không tìm thấy người dùng"
    ),

    POST_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "POST_NOT_FOUND",
        "Không tìm thấy bài đăng"
    ),

    SOCIAL_ACCOUNT_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "SOCIAL_ACCOUNT_NOT_FOUND",
        "Không tìm thấy tài khoản mạng xã hội"
    ),

    INVALID_SOCIAL_ACCOUNT(
        HttpStatus.BAD_REQUEST,
        "INVALID_SOCIAL_ACCOUNT",
        "Có tài khoản mạng xã hội không hợp lệ"
    ),

    DUPLICATE_CLIENT_REQUEST(
        HttpStatus.CONFLICT,
        "DUPLICATE_CLIENT_REQUEST",
        "Yêu cầu tạo bài đăng đã tồn tại"
    ),

    DUPLICATE_SOCIAL_ACCOUNT(
        HttpStatus.CONFLICT,
        "DUPLICATE_SOCIAL_ACCOUNT",
        "Tài khoản mạng xã hội đã tồn tại"
    ),

    INVALID_POST_STATUS(
        HttpStatus.BAD_REQUEST,
        "INVALID_POST_STATUS",
        "Trạng thái bài đăng không hợp lệ"
    ),

    INVALID_REQUEST(
        HttpStatus.BAD_REQUEST,
        "INVALID_REQUEST",
        "Dữ liệu yêu cầu không hợp lệ"
    ),

    API_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "API_NOT_FOUND",
        "Không tìm thấy API"
    ),

    INTERNAL_SERVER_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_SERVER_ERROR",
        "Hệ thống xảy ra lỗi"
    ),
    DATA_INTEGRITY_VIOLATION(
        HttpStatus.CONFLICT,
        "DATA_INTEGRITY_VIOLATION",
        "Dữ liệu vi phạm ràng buộc data base"
    ),
    FACEBOOK_TIMEOUT(
        HttpStatus.GATEWAY_TIMEOUT,
        "FACEBOOK_TIMEOUT",
        "Facebook API bị timeout"
    ),
    FACEBOOK_SERVICE_UNAVAILABLE(
        HttpStatus.SERVICE_UNAVAILABLE,
        "FACEBOOK_SERVICE_UNAVAILABLE",
        "Facebook API tạm thời không khả dụng"
    ),
    FACEBOOK_RATE_LIMIT(
        HttpStatus.TOO_MANY_REQUESTS,
        "FACEBOOK_RATE_LIMIT",
        "Facebook API giới hạn số lượng request"
    ),
    FACEBOOK_BAD_REQUEST(
        HttpStatus.BAD_REQUEST,
        "FACEBOOK_BAD_REQUEST",
        "Nội dung gửi lên Facebook không hợp lệ"
    ),
    FACEBOOK_UNAUTHORIZED(
        HttpStatus.UNAUTHORIZED,
        "FACEBOOK_UNAUTHORIZED",
        "Access token Facebook không hợp lệ hoặc đã hết hạn"
    ),
    TIKTOK_TIMEOUT(
        HttpStatus.GATEWAY_TIMEOUT,
        "TIKTOK_TIMEOUT",
        "TikTok API bị timeout"
    ),
    TIKTOK_SERVICE_UNAVAILABLE(
        HttpStatus.SERVICE_UNAVAILABLE,
        "TIKTOK_SERVICE_UNAVAILABLE",
        "TikTok API tạm thời không khả dụng"
    ),
    TIKTOK_RATE_LIMIT(
        HttpStatus.TOO_MANY_REQUESTS,
        "TIKTOK_RATE_LIMIT",
        "TikTok API giới hạn số lượng request"
    ),
    TIKTOK_BAD_REQUEST(
        HttpStatus.BAD_REQUEST,
        "TIKTOK_BAD_REQUEST",
        "Nội dung gửi lên TikTok không hợp lệ"
    ),
    TIKTOK_UNAUTHORIZED(
        HttpStatus.UNAUTHORIZED,
        "TIKTOK_UNAUTHORIZED",
        "Access token TikTok không hợp lệ hoặc đã hết hạn"
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(
        HttpStatus httpStatus,
        String code,
        String message
    ) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
