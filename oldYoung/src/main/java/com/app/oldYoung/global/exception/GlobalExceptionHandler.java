package com.app.oldYoung.global.exception;

import com.app.oldYoung.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("CustomException: [{}] {} - Context: {}", 
                 errorCode.getCode(), e.getMessage(), e.getContext());
        
        return createErrorResponse(errorCode, e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<Void>> handleValidationException(Exception e) {
        log.error("Validation error: {}", e.getMessage());
        
        String detail = extractValidationError(e);
        return createErrorResponse(ErrorCode.INVALID_INPUT, detail);
    }

    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class,
        HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleClientError(Exception e) {
        log.error("Client error: {}", e.getMessage());
        return createErrorResponse(ErrorCode.INVALID_INPUT, "잘못된 요청입니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected error", e);
        return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, null);
    }

    private ResponseEntity<ApiResponse<Void>> createErrorResponse(ErrorCode errorCode, String detail) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(detail != null ? 
                      ApiResponse.error(errorCode.getCode(), errorCode.getMessage(), detail) :
                      ApiResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }

    private String extractValidationError(Exception e) {
        if (e instanceof MethodArgumentNotValidException) {
            return ((MethodArgumentNotValidException) e).getBindingResult()
                    .getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .findFirst().orElse("유효성 검사 실패");
        }
        if (e instanceof BindException) {
            return ((BindException) e).getBindingResult()
                    .getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .findFirst().orElse("바인딩 실패");
        }
        return "입력값 오류";
    }
}
