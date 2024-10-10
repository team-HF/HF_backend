package com.hf.healthfriend.domain.review.dto.response;

import com.hf.healthfriend.domain.review.constants.EvaluationType;

import java.util.List;

public record RevieweeResponseDto(
        Long memberId,
        double averageScore,
        List<ReviewDto> reviewDetails
) {

    public record ReviewDto(
            EvaluationType evaluationType,
            Long totalCountPerEvaluationType,
            List<ReviewDetailPerEvaluationType> reviewDetailsPerEvaluationType
    ) {
    }

    public record ReviewDetailPerEvaluationType(
            Integer reviewDetailId,
            Long reviewDetailCount
    ) {
    }
}
