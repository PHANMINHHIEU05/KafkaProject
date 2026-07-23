package com.example.exception;

import lombok.Getter;

@Getter
public class ApiException extends AppException {

    private final Integer httpStatusCode;
    private final boolean retryable;

    public ApiException(
        ErrorCode errorCode,
        String message,
        Integer httpStatusCode,
        boolean retryable
    ) {
        super(errorCode, message);
        this.httpStatusCode = httpStatusCode;
        this.retryable = retryable;
    }
}