package com.example.exception;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
