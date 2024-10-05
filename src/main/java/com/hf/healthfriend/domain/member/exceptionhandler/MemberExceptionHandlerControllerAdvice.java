package com.hf.healthfriend.domain.member.exceptionhandler;

import com.hf.healthfriend.domain.member.exception.DuplicateMemberCreationException;
import com.hf.healthfriend.domain.member.exception.FitnessLevelUpdateException;
import com.hf.healthfriend.domain.member.exception.MemberErrorCode;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.global.spec.ApiErrorResponse;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.hf.healthfriend.global.exception.ErrorCode.MEMBER_ALREADY_EXISTS;
import static com.hf.healthfriend.global.exception.ErrorCode.MEMBER_OF_THE_MEMBER_ID_NOT_FOUND;

@Slf4j
@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.member")
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

    @ExceptionHandler(DuplicateMemberCreationException.class)
    public ResponseEntity<ApiErrorResponse> duplicateMemberCreationException(DuplicateMemberCreationException e) {
        log.error("""
                        DuplicateMemberCreationException
                        Error Code: {}
                        HTTP Status: {}
                        message: {}
                        """,
                MEMBER_ALREADY_EXISTS.code(),
                HttpStatus.BAD_REQUEST.value(),
                MEMBER_ALREADY_EXISTS.message(),
                e);
        return ResponseEntity.badRequest()
                .body(
                        ApiErrorResponse.builder()
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .statusCodeSeries(HttpStatus.BAD_REQUEST.series().value())
                                .errorCode(MEMBER_ALREADY_EXISTS.code())
                                .errorName(MEMBER_ALREADY_EXISTS.name())
                                .message(MEMBER_ALREADY_EXISTS.message())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BasicErrorResponse> validationException(MethodArgumentNotValidException e) {
        log.error("ValidationException occurred", e);
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .errorCode(MemberErrorCode.MEMBER_CREATION_VALIDATION_ERROR.code())
                        .errorName(MemberErrorCode.MEMBER_CREATION_VALIDATION_ERROR.name())
                        .message(MemberErrorCode.MEMBER_CREATION_VALIDATION_ERROR.message())
                        .statusCodeSeries(4)
                        .statusCode(MemberErrorCode.MEMBER_CREATION_VALIDATION_ERROR.status())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(FitnessLevelUpdateException.class)
    public ResponseEntity<BasicErrorResponse> fitnessLevelUpdateException(FitnessLevelUpdateException e) {
        log.error("회원 업데이트 중 예외 발생", e);
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .statusCode(MemberErrorCode.FITNESS_LEVEL_UPDATE_NOT_ALLOWED.status())
                        .statusCodeSeries(4)
                        .errorCode(MemberErrorCode.FITNESS_LEVEL_UPDATE_NOT_ALLOWED.code())
                        .errorName(MemberErrorCode.FITNESS_LEVEL_UPDATE_NOT_ALLOWED.name())
                        .message(MemberErrorCode.MEMBER_CREATION_VALIDATION_ERROR.message())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }
}
