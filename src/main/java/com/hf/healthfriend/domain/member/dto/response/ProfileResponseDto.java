package com.hf.healthfriend.domain.member.dto.response;

import com.hf.healthfriend.domain.review.dto.response.SimpleReviewResponseDto;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import lombok.Builder;

import java.util.List;

@Builder
public record ProfileResponseDto(
        Long memberId,
// TODO: 찜하기 기능이 머지되면 추가
//        Long wishedCount,
        String introduction,
        List<SpecDto> specs,
        SimpleReviewResponseDto reviews,
        Double averageReviewScore,
        long matchingCount,
        long reviewCount
) {
}
