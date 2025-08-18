package com.app.oldYoung.global.common.apiResponse.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final Object context;
    
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.context = null;
    }
    
    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.context = null;
    }
    
    public CustomException(ErrorCode errorCode, String message, Object context) {
        super(message);
        this.errorCode = errorCode;
        this.context = context;
    }
    
    public CustomException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.context = null;
    }
    
    public CustomException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = null;
    }
    
    public CustomException(ErrorCode errorCode, String message, Object context, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = context;
    }
}
