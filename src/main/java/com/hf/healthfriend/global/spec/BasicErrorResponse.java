package com.hf.healthfriend.global.spec;

import com.hf.healthfriend.global.exception.ErrorCodeSpecifiable;
import lombok.Builder;

@Builder
public record BasicErrorResponse(
        int statusCode,
        int statusCodeSeries,
        String errorCode,
        String errorName,
        String message,
        Object detail
) {

    public static BasicErrorResponse of(ErrorCodeSpecifiable errorCodeSpecifiable, Object detail) {
        return BasicErrorResponse.builder()
                .errorCode(errorCodeSpecifiable.code())
                .errorName(errorCodeSpecifiable.name())
                .statusCode(errorCodeSpecifiable.status())
                .statusCodeSeries(errorCodeSpecifiable.statusCodeSeries())
                .message(errorCodeSpecifiable.message())
                .detail(detail)
                .build();
    }

    public static BasicErrorResponse of(ErrorCodeSpecifiable errorCodeSpecifiable) {
        return of(errorCodeSpecifiable, null);
    }
}
