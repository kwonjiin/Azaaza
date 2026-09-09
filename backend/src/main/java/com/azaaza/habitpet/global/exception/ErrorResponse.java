package com.azaaza.habitpet.global.exception;

import org.springframework.validation.BindingResult;

import java.util.List;

/** API.md에 정의한 공통 에러 응답 스키마. */
public record ErrorResponse(
        int status,
        String code,
        String message,
        List<FieldErrorDetail> errors
) {

    public record FieldErrorDetail(String field, String reason) {
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getStatus().value(), errorCode.name(), errorCode.getMessage(), List.of());
    }

    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
        List<FieldErrorDetail> errors = bindingResult.getFieldErrors().stream()
                .map(fe -> new FieldErrorDetail(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return new ErrorResponse(errorCode.getStatus().value(), errorCode.name(), errorCode.getMessage(), errors);
    }
}
