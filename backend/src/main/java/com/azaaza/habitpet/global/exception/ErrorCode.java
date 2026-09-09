package com.azaaza.habitpet.global.exception;

import org.springframework.http.HttpStatus;

/**
 * API.md의 공통 에러 응답 표(status/code/message)를 코드로 옮긴 것.
 * 새 에러가 필요하면 여기에만 추가하면 컨트롤러/서비스 어디서든 일관된 응답이 나간다.
 */
public enum ErrorCode {

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    DUPLICATE_RECORD(HttpStatus.CONFLICT, "이미 해당 날짜에 기록이 존재합니다."),

    ANIMAL_NOT_FOUND(HttpStatus.NOT_FOUND, "동물을 찾을 수 없습니다."),
    HABIT_NOT_FOUND(HttpStatus.NOT_FOUND, "습관을 찾을 수 없습니다."),
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "일기를 찾을 수 없습니다."),

    INVALID_HABIT_TARGET(HttpStatus.BAD_REQUEST, "targetType에 맞지 않는 targetValue/targetUnit입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
