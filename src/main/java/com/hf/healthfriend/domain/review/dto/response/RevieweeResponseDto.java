package com.hf.healthfriend.domain.review.dto.response;

import java.util.List;

public record RevieweeResponseDto(
        Long memberId,
        double averageScore,
        List<ReviewResponseDto> reviewDetails
) {
}
