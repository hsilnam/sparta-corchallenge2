package com.sparta.corporatechallenge2.exception;

import com.sparta.corporatechallenge2.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 커스텀 에러 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
//        e.printStackTrace();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponseDto.of(errorCode));
    }

    // @Valid 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(BindException e) {
//        log.error("[EXCEPTION] {}", e.getClass().getSimpleName());
//        e.printStackTrace();
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponseDto(ErrorCode.INVALID_INPUT_VALUE.getCode(), e.getFieldError().getDefaultMessage()));
    }

    // PathVariable 타입 MissMatch
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
//        log.error("[EXCEPTION] {}", e.getClass().getSimpleName());
//        e.printStackTrace();
        return ResponseEntity.status(BAD_REQUEST)
                .body(ErrorResponseDto.of(ErrorCode.INVALID_PATH_VALUE));
    }

    // 잘못된 HttpMethod로 요청
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
//        log.error("[EXCEPTION] {}", e.getClass().getSimpleName());
//        e.printStackTrace();
        return ResponseEntity.status(BAD_REQUEST)
                .body(ErrorResponseDto.of(ErrorCode.METHOD_NOT_ALLOWED));
    }

    // 없는 api로 요청
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoHandlerFoundException(NoResourceFoundException e) {
//        log.error("[EXCEPTION] {}", e.getClass().getSimpleName());
//        e.printStackTrace();
        return ResponseEntity.status(BAD_REQUEST)
                .body(ErrorResponseDto.of(ErrorCode.NOT_EXIST_API));
    }

    // 서버 오류 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleServerError(Exception e) {
        // 서버 내부 로그 기록
        log.error("[EXCEPTION] {}", e.getClass().getSimpleName());
        e.printStackTrace();

        // 서버 내부 오류 메시지는 클라이언트에 노출하지 않음
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDto.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}