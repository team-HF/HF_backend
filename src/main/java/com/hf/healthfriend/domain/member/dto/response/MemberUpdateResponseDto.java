package com.hf.healthfriend.domain.member.dto.response;

import com.hf.healthfriend.domain.member.constant.*;
import lombok.Builder;

@Builder
public record MemberUpdateResponseDto(
        Long id,
        String profileImageUrl,
        String location,
        String introduction,
        FitnessLevel fitnessLevel,
        CompanionStyle companionStyle,
        FitnessEagerness fitnessEagerness,
        FitnessObjective fitnessObjective,
        FitnessKind fitnessKind
) {
}
