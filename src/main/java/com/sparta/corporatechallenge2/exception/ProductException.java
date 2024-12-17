package com.sparta.corporatechallenge2.exception;

public class ProductException extends CustomException {
    public ProductException(ErrorCode errorCode) {
        super(errorCode);
    }
}
