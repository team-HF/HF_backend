package com.hf.healthfriend.global.exception;

import com.hf.healthfriend.global.exception.custom.MemberException;
import com.hf.healthfriend.global.spec.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExControllerAdvice {


    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ApiErrorResponse> handleMemberException(MemberException e){
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(e.getErrorCode().getCode())
                .message(e.getErrorCode().getMessage())
                .build();

        return ResponseEntity.status(apiErrorResponse.getCode()).body(apiErrorResponse);
    }

    /**
     * Http Method가 잘못 됨 -> Get인데 Post 등
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(405)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(405).body(apiErrorResponse);
    }

    /**
     * Api 요청 스펙이 잘못 됨 -> Json 형식으로 안보냄
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(400)
                .message("요청 데이터 형식이 잘못 되었습니다. API 요청 스펙을 다시 확인해주세요 : " + e.getMessage())
                .build();

        log.info("에러 메시지 : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e){
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(400)
                .message("쿼리 파라미터에 값이 없습니다")
                .build();

        log.info("에러 메시지 : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    /**
     * Validation 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        String errorMessages = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(400)
                .message(errorMessages)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpClientErrorException(HttpClientErrorException e){
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(400)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e){
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(400)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorResponse);
    }
}
