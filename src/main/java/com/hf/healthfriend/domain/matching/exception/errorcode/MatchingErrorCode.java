package com.hf.healthfriend.domain.matching.exception.errorcode;

import com.hf.healthfriend.global.exception.ErrorCodeSpecifiable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MatchingErrorCode implements ErrorCodeSpecifiable {
    OUT_OF_LIMIT_MATCHING_REQUEST(40001, "MAT001", "매칭 신청 한도를 초과했습니다."),
    MATCHING_NOT_FOUND(40401, "MAT002", "매칭이 존재하지 않습니다.");

    private final int status;
    private final String code;
    private final String message;

    @Override
    public int status() {
        return this.status;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }

    @Override
    public int statusCodeSeries() {
        return this.status / 10000;
    }
}
