package com.hf.healthfriend.global.spec;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class ApiBasicResponse<D> implements Serializable {
    private final int statusCode;
    private final int statusCodeSeries;
    private final String message;
    private final D content;

    public static <D> ApiBasicResponse<D> of(HttpStatus httpStatus) {
        return of(null, httpStatus, null);
    }

    public static <D> ApiBasicResponse<D> of(D content, HttpStatus httpStatus) {
        return of(content, httpStatus, null);
    }

    public static <D> ApiBasicResponse<D> of(HttpStatus httpStatus, String message) {
        return of(null, httpStatus, message);
    }

    public static <D> ApiBasicResponse<D> of(D content, HttpStatus httpStatus, String message) {
        return new ApiBasicResponse<>(
                httpStatus.value(),
                httpStatus.series().value(),
                message,
                content
        );
    }
}

