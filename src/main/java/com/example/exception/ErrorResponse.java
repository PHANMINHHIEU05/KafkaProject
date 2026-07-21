package com.example.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(

    Instant timestamp,

    int status,

    String error,

    String code,

    String message,

    String path,

    Map<String, String> validationErrors

) {
}