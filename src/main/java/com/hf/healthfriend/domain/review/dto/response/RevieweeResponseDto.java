package com.hf.healthfriend.domain.review.dto.response;

public record RevieweeResponseDto(
        Long memberId,
        ReviewResponseDto good,
        ReviewResponseDto notGood,
        double averageScore
) {
}
