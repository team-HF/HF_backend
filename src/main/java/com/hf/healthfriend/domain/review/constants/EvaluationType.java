package com.hf.healthfriend.domain.review.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EvaluationType {
    GOOD("좋아요"),
    NOT_GOOD("아쉬워요");

    private final String value;
}
