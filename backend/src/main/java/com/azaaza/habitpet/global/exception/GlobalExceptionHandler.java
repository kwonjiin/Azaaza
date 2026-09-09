package com.azaaza.habitpet.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 모든 컨트롤러의 예외를 한곳에서 API.md 스키마로 변환한다.
 * 컨트롤러 코드에서 try/catch가 사라지는 대신, "이 API가 어떤 에러를 던질 수 있는가"는
 * 서비스 코드의 BusinessException(ErrorCode...) 호출을 보고 파악하는 구조.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.of(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        ErrorResponse response = ErrorResponse.of(ErrorCode.VALIDATION_ERROR, e.getBindingResult());
        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e) {
        // 운영에서는 여기서 로깅(+ 알림 연동)을 반드시 넣어야 한다. MVP는 로그만 남기고 500 고정 응답.
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(500, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다.", List.of()));
    }
}
