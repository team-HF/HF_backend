package com.hf.healthfriend.domain.member.dto.request;

import com.hf.healthfriend.domain.member.constant.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class MemberCreationRequestDto {

    @Schema(description = "생성할 회원의 ID. 소셜 로그인일 경우 id와 email은 같다. email은 id로 설정된다.")
    private String id;

    private String nickname;

    @Schema(description = "yyyy-mm-dd 형식 ex) 2017-08-09")
    private LocalDate birthDate;

    @Schema(description = "성별 - 남자 = MALE / 여자 = FEMALE")
    private Gender gender;
    
    private String introduction;

    @Schema(description = "운동 레벨 - 새싹 = BEGINNER / 고수 = ADVANCED")
    private FitnessLevel fitnessLevel;

    @Schema(description = "운동할 때 주로 누구랑? - 소규모형 = SMALL / 그룹형 = GROUP")
    private CompanionStyle companionStyle;

    @Schema(description = "운동할 때 나는 평소? - 의욕만렙형 = EAGER / 귀차니즘형 = LAZY")
    private FitnessEagerness fitnessEagerness;

    @Schema(description = "나의 운동 목적은? - 벌크업 = BULK_UP / 러닝러닝 = RUNNING")
    private FitnessObjective fitnessObjective;

    @Schema(description = "주로 하고 있는 운동은? - 고강도 운동 위주 = HIGH_STRESS / 기능성 피트니스 위주 = FUNCTIONAL")
    private FitnessKind fitnessKind;
}
