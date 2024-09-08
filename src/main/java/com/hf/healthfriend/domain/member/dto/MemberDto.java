package com.hf.healthfriend.domain.member.dto;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.entity.Member;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class MemberDto {
    private String memberId;
    private Role role;
    private String email;
    private LocalDateTime creationTime;
    private String nickname;
    private LocalDate birthDate;
    private Gender gender;
    private String introduction;
    private FitnessLevel fitnessLevel;
    private CompanionStyle companionStyle;
    private FitnessEagerness fitnessEagerness;
    private FitnessObjective fitnessObjective;
    private FitnessKind fitnessKind;

    public static MemberDto of(Member member) {
        return MemberDto.builder()
                .memberId(member.getId())
                .role(member.getRole())
                .email(member.getEmail())
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
