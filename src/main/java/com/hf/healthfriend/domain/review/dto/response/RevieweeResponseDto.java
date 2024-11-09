package com.hf.healthfriend.domain.review.dto.response;

import java.util.List;

public record RevieweeResponseDto(
        Long memberId,
        List<ReviewResponseDto> reviewDetails
) {
}
