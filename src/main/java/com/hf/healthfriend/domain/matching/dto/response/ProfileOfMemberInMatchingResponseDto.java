package com.hf.healthfriend.domain.matching.dto.response;

import com.hf.healthfriend.domain.member.constant.CompanionStyle;
import com.hf.healthfriend.domain.member.constant.FitnessEagerness;
import com.hf.healthfriend.domain.member.constant.FitnessKind;
import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import lombok.Builder;

@Builder
public record ProfileOfMemberInMatchingResponseDto(
        Long memberId,
        String nickname,
        String profileImageUrl,
        FitnessLevel fitnessLevel,
        CompanionStyle companionStyle,
        FitnessEagerness fitnessEagerness,
        FitnessKind fitnessKind,
        String cd1,
        String cd2,
        String cd3,
        Long matchedCount
) {
}
