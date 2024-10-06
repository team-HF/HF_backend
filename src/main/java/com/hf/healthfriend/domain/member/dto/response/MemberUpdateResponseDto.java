package com.hf.healthfriend.domain.member.dto.response;

import com.hf.healthfriend.domain.member.constant.*;
import lombok.Builder;

@Builder(toBuilder = true)
public record MemberUpdateResponseDto(
        String profileImageUrl,
        String city,
        String district,
        String introduction,
        FitnessLevel fitnessLevel,
        CompanionStyle companionStyle,
        FitnessEagerness fitnessEagerness,
        FitnessObjective fitnessObjective,
        FitnessKind fitnessKind
) {
}
