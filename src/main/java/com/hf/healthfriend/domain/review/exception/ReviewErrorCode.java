package com.hf.healthfriend.domain.review.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ReviewErrorCode {
    MATCHING_NOT_FOUND(40000, "R001", "리뷰를 남기려는 매칭이 존재하지 않음"),
    MEMBER_NOT_FOUND(40001, "R002", "리뷰를 남기려는 회원이 존재하지 않음"),
    INVALID_EVALUATIONS(40002, "R003", "리뷰 평가가 적합하지 않습니다.")
    ;

    private final int status;
    private final String code;
    private final String message;

    public int status() {
        return this.status;
    }

    public String code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}
