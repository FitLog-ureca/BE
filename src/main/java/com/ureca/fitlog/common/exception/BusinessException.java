package com.ureca.fitlog.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 예외
 * ExceptionStatus를 받아서 통일된 예외 처리
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ExceptionStatus status;

    public BusinessException(ExceptionStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public BusinessException(ExceptionStatus status, Throwable cause) {
        super(status.getMessage(), cause);
        this.status = status;
    }
}