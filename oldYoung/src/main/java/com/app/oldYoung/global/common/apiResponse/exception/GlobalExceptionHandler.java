package com.app.oldYoung.global.common.apiResponse.exception;

import com.app.oldYoung.global.common.apiResponse.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("CustomException: [{}] {} - Context: {}", 
                 errorCode.getCode(), e.getMessage(), e.getContext());
        
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation error: {}", e.getMessage());
        
        String errorDetails = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE.getCode(), 
                                      ErrorCode.INVALID_INPUT_VALUE.getMessage(), 
                                      errorDetails));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("Type mismatch: parameter={}, value={}", e.getPropertyName(), e.getValue());
        
        String detail = String.format("'%s' 파라미터 타입 오류", e.getPropertyName());
        
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ErrorCode.INVALID_TYPE_VALUE.getCode(), 
                                      ErrorCode.INVALID_TYPE_VALUE.getMessage(), detail));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameterException(MissingServletRequestParameterException e) {
        log.error("Missing parameter: {}", e.getParameterName());
        
        String detail = String.format("'%s' 파라미터가 필요합니다", e.getParameterName());
        
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ErrorCode.MISSING_REQUEST_PARAMETER.getCode(), 
                                      ErrorCode.MISSING_REQUEST_PARAMETER.getMessage(), detail));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HTTP message not readable: {}", e.getMessage());
        
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ErrorCode.INVALID_FORMAT.getCode(), 
                                      ErrorCode.INVALID_FORMAT.getMessage(), "JSON 형식 오류"));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(DataAccessException e) {
        log.error("Database error", e);
        
        return ResponseEntity
                .status(500)
                .body(ApiResponse.error(ErrorCode.DATABASE_ERROR.getCode(), 
                                      ErrorCode.DATABASE_ERROR.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected error", e);
        
        return ResponseEntity
                .status(500)
                .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), 
                                      ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
