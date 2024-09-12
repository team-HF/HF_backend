package com.hf.healthfriend.domain.member.repository.dto;

import com.hf.healthfriend.domain.member.constant.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Setter
@Getter
@ToString
public class MemberUpdateDto {
    private Role role;
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
