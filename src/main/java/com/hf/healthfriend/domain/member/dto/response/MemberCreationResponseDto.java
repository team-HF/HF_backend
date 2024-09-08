package com.hf.healthfriend.domain.member.dto.response;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.entity.Member;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class MemberCreationResponseDto {
    private final String memberId;
    private final String email;
    private final String role;
    private final LocalDateTime creationTime;
    private final String nickname;
    private final LocalDate birthDate;
    private final Gender gender;
    private final String introduction;
    private final FitnessLevel fitnessLevel;
    private final CompanionStyle companionStyle;
    private final FitnessEagerness fitnessEagerness;
    private final FitnessObjective fitnessObjective;
    private final FitnessKind fitnessKind;

    public static MemberCreationResponseDto of(Member member) {
        return MemberCreationResponseDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .role(member.getRole().name())
                .creationTime(member.getCreationTime())
                .nickname(member.getNickname())
                .birthDate(member.getBirthDate())
                .gender(member.getGender())
                .introduction(member.getIntroduction())
                .fitnessLevel(member.getFitnessLevel())
                .companionStyle(member.getCompanionStyle())
                .fitnessEagerness(member.getFitnessEagerness())
                .fitnessObjective(member.getFitnessObjective())
                .fitnessKind(member.getFitnessKind())
                .build();
    }
}
