package com.hf.healthfriend.domain.member.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record MemberRecommendResponse(
        String profileImageUrl,
        String nickName,
        long matchingCount,
        String introduction,
        List<String> fitnessType
) {
}
