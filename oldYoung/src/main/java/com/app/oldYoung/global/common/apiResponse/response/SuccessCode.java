package com.app.oldYoung.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {
    
    SUCCESS(HttpStatus.OK, "S200", "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "S201", "리소스가 성공적으로 생성되었습니다."),
    UPDATED(HttpStatus.OK, "S202", "리소스가 성공적으로 수정되었습니다."),
    DELETED(HttpStatus.OK, "S203", "리소스가 성공적으로 삭제되었습니다.");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
    
    SuccessCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
