package com.azaaza.habitpet.global.exception;

import lombok.Getter;

/**
 * 서비스 레이어에서 "예상 가능한" 실패를 표현하는 예외.
 * 컨트롤러는 이 예외를 잡지 않고 GlobalExceptionHandler에 위임한다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
