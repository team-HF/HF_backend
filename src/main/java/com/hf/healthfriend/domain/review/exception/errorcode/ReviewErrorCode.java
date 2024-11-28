package com.hf.healthfriend.domain.review.exception.errorcode;

import com.hf.healthfriend.global.exception.ErrorCodeSpecifiable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ReviewErrorCode implements ErrorCodeSpecifiable {
    INVALID_EVALUATIONS(40001, "RV001", "리뷰 평가가 적합하지 않습니다."),
    REVIEW_BEFORE_MEETING(40002, "RV002", "미팅 성사 이전에 리뷰를 남길 수 없습니다"),
    ILLEGAL_MATCHING_STATE(40003, "RV003", "리뷰를 남길 수 있는 매칭 상태가 아닙니다"),
    DUPLICATE_REVIEW(40004, "RV004", "리뷰를 중복해서 올릴 수 없습니다")
    ;

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
