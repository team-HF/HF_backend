package com.hf.healthfriend.domain.review.dto.response;

import java.util.List;

public record SimpleReviewResponseDto(
        List<ReviewDetailPerEvaluationType> good,
        List<ReviewDetailPerEvaluationType> notGood
) {
}
