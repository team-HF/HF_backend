package com.hf.healthfriend.global.spec;

import lombok.Builder;

@Builder
public record BasicErrorResponse(
        int statusCode,
        int statusCodeSeries,
        String errorCode,
        String errorName,
        String message
) {
}
