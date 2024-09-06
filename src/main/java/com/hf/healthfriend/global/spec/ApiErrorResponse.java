package com.hf.healthfriend.global.spec;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public class ApiErrorResponse {
    private final int statusCode;
    private final int statusCodeSeries;
    private final int errorCode;
    private final String errorName;
    private final String message;
}