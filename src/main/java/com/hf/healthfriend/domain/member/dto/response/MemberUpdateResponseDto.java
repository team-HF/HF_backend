package com.hf.healthfriend.domain.member.dto.response;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.spec.dto.response.SpecUpdateResponseDto;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record MemberUpdateResponseDto(
        Long memberId,
        String loginId,

        Role role,
        String email,
        LocalDateTime creationTime,
        String nickname,
        String profileImageUrl,
        LocalDate birthDate,
        Gender gender,
        String introduction,
        FitnessLevel fitnessLevel,
        CompanionStyle companionStyle,
        FitnessEagerness fitnessEagerness,
        FitnessObjective fitnessObjective,
        FitnessKind fitnessKind,

        SpecUpdateResponseDto specUpdateResult
) {
}
