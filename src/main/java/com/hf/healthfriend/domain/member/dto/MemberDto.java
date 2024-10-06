package com.hf.healthfriend.domain.member.dto;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.global.util.mapping.BeanMapping;
import com.hf.healthfriend.global.util.mapping.MappingAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Getter
@ToString
@BeanMapping(Member.class)
public class MemberDto {

    @MappingAttribute(target = "id")
    private Long memberId;

    @Schema(description = "로그인할 때 사용되는 ID. 소셜 로그인일 경우, 소셜 로그인 email")
    private String loginId;

    private Role role;
    private String name;
    private String email;
    private LocalDateTime creationTime;
    private String nickname;
    private String profileImageUrl;
    private String city;
    private String district;
    private LocalDate birthDate;
    private Gender gender;
    private String introduction;
    private FitnessLevel fitnessLevel;
    private CompanionStyle companionStyle;
    private FitnessEagerness fitnessEagerness;
    private FitnessObjective fitnessObjective;
    private FitnessKind fitnessKind;
}
