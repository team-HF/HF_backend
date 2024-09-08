package com.hf.healthfriend.global.spec;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class ApiBasicResponse<D> implements Serializable {
    private int statusCode;
    private int statusCodeSeries;
    private String message;
    private D content;

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

