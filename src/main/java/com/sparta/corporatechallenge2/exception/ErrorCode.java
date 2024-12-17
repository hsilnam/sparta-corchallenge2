package com.sparta.corporatechallenge2.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 일반
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력 값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "사용할 수 없는 메서드입니다."),
    NOT_EXIST_API(HttpStatus.BAD_REQUEST, "C003", "요청 주소가 올바르지 않습니다."),
    INVALID_PATH_VALUE(HttpStatus.BAD_REQUEST, "C004", "요청이 잘못됐습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C005", "서버 에러"),


    // 상품
    PRODUCT_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST, "P001", "해당 상품이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
