package com.app.oldYoung.global.common.apiResponse.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // System Errors (E100~E199)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E100", "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E101", "데이터베이스 오류가 발생했습니다."),
    PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E102", "데이터 파싱 중 오류가 발생했습니다."),

    // Validation Errors (E200~E299)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E200", "입력값이 올바르지 않습니다."),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "E201", "필수 파라미터가 누락되었습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "E202", "데이터 타입이 올바르지 않습니다."),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "E203", "데이터 형식이 올바르지 않습니다."),

    // Authentication & Authorization Errors (E300~E399)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E300", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "E301", "접근 권한이 없습니다."),
    OAUTH_TOKEN_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E302", "OAuth 토큰 요청에 실패했습니다."),
    OAUTH_PROFILE_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E303", "OAuth 프로필 요청에 실패했습니다."),
    JWT_TOKEN_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E304", "JWT 토큰 생성에 실패했습니다."),
    JWT_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "E305", "JWT 토큰이 만료되었습니다."),
    JWT_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "E306", "유효하지 않은 JWT 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "E307", "리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "E308", "유효하지 않은 리프레시 토큰입니다."),

    // Business Logic Errors (E400~E499)
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "E400", "요청한 데이터를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E401", "사용자를 찾을 수 없습니다."),
    DUPLICATE_ENTITY(HttpStatus.CONFLICT, "E402", "중복된 데이터입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
