package com.hf.healthfriend.global.spec;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@RequiredArgsConstructor
@Builder
@Getter
@ToString
public class ApiErrorResponse implements Serializable {
    private final int statusCode;
    private final int statusCodeSeries;
    private final int errorCode;
    private final String errorName;
    private final String message;
}