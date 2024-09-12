package com.hf.healthfriend.domain.member.dto.request;

import com.hf.healthfriend.domain.member.constant.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Setter
@Getter
@ToString
public class MemberUpdateRequestDto {
    private Role role;
    private String nickname;
    private MultipartFile profileImage;
    private LocalDate birthDate;
    private Gender gender;
    private String introduction;
    private FitnessLevel fitnessLevel;
    private CompanionStyle companionStyle;
    private FitnessEagerness fitnessEagerness;
    private FitnessObjective fitnessObjective;
    private FitnessKind fitnessKind;
}
