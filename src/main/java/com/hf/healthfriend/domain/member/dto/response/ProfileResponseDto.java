package com.hf.healthfriend.domain.member.dto.response;

import com.hf.healthfriend.domain.review.dto.response.SimpleReviewResponseDto;
import com.hf.healthfriend.domain.spec.dto.SpecDto;

import java.util.List;

public record ProfileResponseDto(
        Long memberId,
        Double reviewScore,
// TODO: 찜하기 기능이 머지되면 추가
//        Long wishedCount,
        String introduction,
        List<SpecDto> specs,
        List<SimpleReviewResponseDto> reviews
) {
}
