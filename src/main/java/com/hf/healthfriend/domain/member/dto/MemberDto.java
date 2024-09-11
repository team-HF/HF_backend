package com.hf.healthfriend.domain.member.dto;

import com.hf.healthfriend.domain.member.constant.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class MemberDto {
    private String memberId;
    private Role role;
    private String email;
    private LocalDateTime creationTime;
    private String nickname;
    private String profileImageUrl;
    private LocalDate birthDate;
    private Gender gender;
    private String introduction;
    private FitnessLevel fitnessLevel;
    private CompanionStyle companionStyle;
    private FitnessEagerness fitnessEagerness;
    private FitnessObjective fitnessObjective;
    private FitnessKind fitnessKind;
}
