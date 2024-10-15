package com.hf.healthfriend.domain.review.dto.response;

import com.hf.healthfriend.domain.review.constants.EvaluationType;

import java.util.List;

public record ReviewResponseDto(
        EvaluationType evaluationType,
        Long totalCountPerEvaluationType,
        List<ReviewDetailPerEvaluationType> reviewDetailsPerEvaluationType
) {
}
