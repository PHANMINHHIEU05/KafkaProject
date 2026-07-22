package com.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice // lưới chặn lỗi toàn cuc
@Slf4j 
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class /* khi gặp thằng này hoặc các thằng con nó đã viết thì chạy hàm dưới */)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex , HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("errorCode: {}, message: {} , path: {}" , errorCode, ex.getMessage(), request.getRequestURI());
        return buildErrorResponse(
            errorCode.getHttpStatus(),
            errorCode.getCode(),
            ex.getMessage(),
            request,
            null
        );
    }
    // bắt lỗi 400 cho dữ liệu đầu vào 
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodAgrumentNotValidException(MethodArgumentNotValidException ex , HttpServletRequest request) {
        Map<String , String> validationErrors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()){
            validationErrors.putIfAbsent(fieldError.getField() , fieldError.getDefaultMessage());
        }
        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ErrorCode.INVALID_REQUEST.getCode(),
            ErrorCode.INVALID_REQUEST.getMessage(),
            request,
            validationErrors
        );
    }
    
    // bắt lỗi dữ liệu đầu vào cho tham số đầu vào trong các QueryParam , PathVariable , RequestParam
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex , HttpServletRequest request) {
        Map<String , String> validationErrors = new LinkedHashMap<>();
        for (var violation : ex.getConstraintViolations()){
            validationErrors.putIfAbsent(violation.getPropertyPath().toString() , violation.getMessage());
        }
        return buildErrorResponse (
            HttpStatus.BAD_REQUEST,
            ErrorCode.INVALID_REQUEST.getCode(),
            ErrorCode.INVALID_REQUEST.getMessage(),
            request,
            validationErrors
        );
    }

    // bắt lỗi dữ liệu đầu vào cho các trường hợp vi phạm ràng buộc dữ liệu trong cơ sở dữ liệu
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex , HttpServletRequest request) {
        log.error("DataIntegrityViolationException: {} , path: {}" , request.getRequestURL() , ex);
        return buildErrorResponse(
            HttpStatus.CONFLICT,
            ErrorCode.DATA_INTEGRITY_VIOLATION.getCode(),
            ErrorCode.DATA_INTEGRITY_VIOLATION.getMessage(),
            request,
            null
        );
    }

    @ExceptionHandler({
        NoResourceFoundException.class,
        NoHandlerFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(Exception ex, HttpServletRequest request) {
        log.warn("API not found: method={}, path={}", request.getMethod(), request.getRequestURI());
        return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            ErrorCode.API_NOT_FOUND.getCode(),
            "Không tìm thấy API: " + request.getRequestURI(),
            request,
            null
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex,
        HttpServletRequest request
    ) {
        log.warn("Invalid JSON request: path={}, message={}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ErrorCode.INVALID_REQUEST.getCode(),
            "JSON không hợp lệ hoặc sai kiểu dữ liệu. Nếu không hẹn giờ, hãy gửi scheduledAt là null không có dấu ngoặc kép.",
            request,
            null
        );
    }

    // bắt tất cả thằng khác , những trường hợp không lường trước được 
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex , HttpServletRequest request) {
        log.error("Unexpected exception  at path = {}" , request.getRequestURL() , ex);
        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
            request,
            null
        );
    }
    private ResponseEntity<ErrorResponse> buildErrorResponse(
        HttpStatus status,
        String code,
        String message, 
        HttpServletRequest request,
        Map<String , String> validatioinErrors
    ) {
        ErrorResponse reponse = new ErrorResponse(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            code,
            message,
            request.getRequestURI(),
            validatioinErrors
        );
        return ResponseEntity.status(status).body(reponse);
    }
}
