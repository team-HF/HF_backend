package com.hf.healthfriend.domain.member.exceptionhandler;

import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.global.spec.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.hf.healthfriend.global.exception.ErrorCode.MEMBER_OF_THE_MEMBER_ID_NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class MemberExceptionHandlerControllerAdvice {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> noSuchElementException(MemberNotFoundException e) {
        log.error("""
                MemberNotFoundException
                Error Code: {}
                HTTP Status: {}
                message: {}
                """,
                MEMBER_OF_THE_MEMBER_ID_NOT_FOUND.code(),
                HttpStatus.NOT_FOUND.value(),
                MEMBER_OF_THE_MEMBER_ID_NOT_FOUND.message(),
                e);

        return new ResponseEntity<>(
                ApiErrorResponse.builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .statusCodeSeries(HttpStatus.NOT_FOUND.series().value())
                        .errorCode(MEMBER_OF_THE_MEMBER_ID_NOT_FOUND.code())
                        .errorName(MEMBER_OF_THE_MEMBER_ID_NOT_FOUND.name())
                        .message(MEMBER_OF_THE_MEMBER_ID_NOT_FOUND.message())
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }
}
