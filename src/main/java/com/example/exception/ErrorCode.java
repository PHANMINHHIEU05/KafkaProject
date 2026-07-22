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
        HttpStatus.BAD_REQUEST,
        "DATA_INTEGRITY_VIOLATION",
        "Dữ liệu vi phạm ràng buộc data base"
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
