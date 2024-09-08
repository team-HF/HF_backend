package com.hf.healthfriend.domain.member.dto;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.entity.Profile;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class ProfileDto {
    private String memberId;
    private String nickname;
    private LocalDate birthDate;
    private Gender gender;
    private String introduction;
    private FitnessLevel fitnessLevel;
    private CompanionStyle companionStyle;
    private FitnessEagerness fitnessEagerness;
    private FitnessObjective fitnessObjective;
    private FitnessKind fitnessKind;

    public static ProfileDto of(Profile profile) {
        var dto = new ProfileDto();
        BeanUtils.copyProperties(profile, dto);
        dto.memberId = profile.getMember().getId();
        return dto;
    }
}
