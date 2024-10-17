package com.hf.healthfriend.domain.member.dto.response;

import com.hf.healthfriend.domain.member.constant.*;
import lombok.Builder;

@Builder(toBuilder = true)
public record MemberUpdateResponseDto(
        String profileImageUrl,
        String cd1,
        String cd2,
        String cd3,
        String introduction,
        FitnessLevel fitnessLevel,
        CompanionStyle companionStyle,
        FitnessEagerness fitnessEagerness,
        FitnessObjective fitnessObjective,
        FitnessKind fitnessKind
) {
}
