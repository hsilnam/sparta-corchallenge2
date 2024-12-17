package com.sparta.corporatechallenge2.exception;

import lombok.Getter;

@Getter
public abstract class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    protected CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}