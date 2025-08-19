package com.app.oldYoung.global.common.apiResponse.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private final boolean success;
    private final String code;
    private final String message;
    private final T data;
    private final ErrorDetail error;
    private final String traceId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    private ApiResponse(boolean success, String code, String message, T data, 
                       ErrorDetail error, String traceId) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.error = error;
        this.traceId = traceId;
        this.timestamp = LocalDateTime.now();
    }

    // === Success Response Methods ===
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, SuccessCode.SUCCESS.getCode(), 
                               SuccessCode.SUCCESS.getMessage(), data, null, generateTraceId());
    }

    public static <T> ApiResponse<T> success(SuccessCode successCode, T data) {
        return new ApiResponse<>(true, successCode.getCode(), 
                               successCode.getMessage(), data, null, generateTraceId());
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, SuccessCode.SUCCESS.getCode(), 
                               message, null, null, generateTraceId());
    }

    // === Error Response Methods ===
    
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, code, null, null, 
                               new ErrorDetail(code, message, null), generateTraceId());
    }

    public static <T> ApiResponse<T> error(String code, String message, String detail) {
        return new ApiResponse<>(false, code, null, null, 
                               new ErrorDetail(code, message, detail), generateTraceId());
    }

    private static String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetail {
        private final String code;
        private final String message;
        private final String detail;

        public ErrorDetail(String code, String message, String detail) {
            this.code = code;
            this.message = message;
            this.detail = detail;
        }
    }
}
