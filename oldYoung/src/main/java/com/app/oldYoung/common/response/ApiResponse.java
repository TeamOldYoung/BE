package com.app.oldYoung.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private final boolean success;
    private final String message;
    private final T data;
    private final ErrorInfo error;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    private ApiResponse(boolean success, String message, T data, ErrorInfo error) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "성공", data, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, null, new ErrorInfo(code, message, null));
    }

    public static <T> ApiResponse<T> error(String code, String message, String detail) {
        return new ApiResponse<>(false, null, null, new ErrorInfo(code, message, detail));
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorInfo {
        private final String code;
        private final String message;
        private final String detail;

        public ErrorInfo(String code, String message, String detail) {
            this.code = code;
            this.message = message;
            this.detail = detail;
        }
    }
}
