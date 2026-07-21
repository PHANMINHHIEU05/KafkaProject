package com.example.exception;

public class BadRequestException extends AppException {

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BadRequestException(
        ErrorCode errorCode,
        String message
    ) {
        super(errorCode, message);
    }
}