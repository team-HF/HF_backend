package com.hf.healthfriend.domain.review.dto.response;

import com.hf.healthfriend.domain.review.constants.EvaluationType;

import java.util.List;

public record SimpleReviewResponseDto(
        EvaluationType evaluationType,
        List<ReviewDetailPerEvaluationType> reviewDetailsPerEvaluationType
) {
}
