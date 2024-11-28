package com.hf.healthfriend.domain.member.exception.handler;

import com.hf.healthfriend.domain.member.exception.DuplicateMemberCreationException;
import com.hf.healthfriend.domain.member.exception.FitnessLevelUpdateException;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.hf.healthfriend.domain.member.exception.errorcode.MemberErrorCode.*;

@Slf4j
@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.member")
public class MemberExceptionHandlerControllerAdvice {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> noSuchElementException(MemberNotFoundException e) {
        log.error("""
                        MemberNotFoundException
                        Error Code: {}
                        HTTP Status: {}
                        message: {}
                        """,
                MEMBER_NOT_FOUND.code(),
                HttpStatus.NOT_FOUND.value(),
                MEMBER_NOT_FOUND.message(),
                e);

        return new ResponseEntity<>(
                BasicErrorResponse.of(MEMBER_NOT_FOUND),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DuplicateMemberCreationException.class)
    public ResponseEntity<BasicErrorResponse> duplicateMemberCreationException(DuplicateMemberCreationException e) {
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
                        BasicErrorResponse.of(MEMBER_NOT_FOUND)
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BasicErrorResponse> validationException(MethodArgumentNotValidException e) {
        log.info("ValidationException occurred", e);
        if (log.isDebugEnabled()) {
            log.debug("{}", e.getBindingResult());
        }

        return ResponseEntity.badRequest()
                .body(BasicErrorResponse.of(MEMBER_CREATION_VALIDATION_ERROR));
    }

    @ExceptionHandler(FitnessLevelUpdateException.class)
    public ResponseEntity<BasicErrorResponse> fitnessLevelUpdateException(FitnessLevelUpdateException e) {
        log.info("회원 업데이트 중 예외 발생", e);

        return ResponseEntity.badRequest()
                .body(BasicErrorResponse.of(FITNESS_LEVEL_UPDATE_NOT_ALLOWED));
    }
}
