package com.hf.healthfriend.domain.member.dto.response;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class MemberCreationResponseDto {

    @Schema(description = "생성된 회원의 ID")
    private final Long memberId;

    @Schema(description = "login용 ID. 소셜 로그인일 경우 email과 값이 같다.")
    private final String loginId;

    @Schema(description = "생성된 회원의 Email. 소셜 로그인일 경우 memberId와 email은 같다.")
    private final String email;

    @Schema(description = "권한 설정 때 사용하는 Role. 기본적으로 Role은 자동 지정", defaultValue = "ROLE_MEMBER")
    private final String role;

    private final LocalDateTime creationTime;
    private final String nickname;
    private final LocalDate birthDate;
    private final Gender gender;
    private final String introduction;

    @Schema(description = "운동 레벨 - 새싹 = BEGINNER / 고수 = ADVANCED")
    private final FitnessLevel fitnessLevel;

    @Schema(description = "운동할 때 주로 누구랑? - 소규모형 = SMALL / 그룹형 = GROUP")
    private final CompanionStyle companionStyle;

    @Schema(description = "운동할 때 나는 평소? - 의욕만렙형 = EAGER / 귀차니즘형 = LAZY")
    private final FitnessEagerness fitnessEagerness;

    @Schema(description = "나의 운동 목적은? - 벌크업 = BULK_UP / 러닝러닝 = RUNNING")
    private final FitnessObjective fitnessObjective;

    @Schema(description = "주로 하고 있는 운동은? - 고강도 운동 위주 = HIGH_STRESS / 기능성 피트니스 위주 = FUNCTIONAL")
    private final FitnessKind fitnessKind;

    @Schema(description = "등록된 경력 및 수상 이력의 자동 생성된 ID 리스트")
    private final List<Long> specIds;

    public static MemberCreationResponseDto of(Member member, List<Long> specIds) {
        return MemberCreationResponseDto.builder()
                .memberId(member.getId())
                .loginId(member.getLoginId())
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
                .specIds(specIds)
                .build();
    }
}
