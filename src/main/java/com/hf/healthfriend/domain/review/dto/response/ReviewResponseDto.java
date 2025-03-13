package com.hf.healthfriend.domain.review.dto.response;

import java.util.List;

public record ReviewResponseDto(
        Long totalCountPerEvaluationType,
        List<ReviewDetailPerEvaluationType> reviewDetailsPerEvaluationType
) {
}
