package com.hf.healthfriend.domain.member.dto.request;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Setter // multipart/form-data 받을 땐 Setter가 필요하다고 한다.
@Getter
@ToString
@EqualsAndHashCode
public class MemberCreationRequestDto {

    @Schema(description = "생성할 회원의 ID. 소셜 로그인일 경우 id와 email은 같다. email은 id로 설정된다.")
    @NotEmpty
    private String id;

    @Schema(description = "회원의 이름")
    @NotEmpty
    private String name;

    @Length(min = 1, max = 8)
    @NotNull
    private String nickname;

    @Schema(description = "회원 프로필 이미지")
    private MultipartFile profileImage;

    @Schema(description = "yyyy-mm-dd 형식 ex) 2017-08-09")
    @Past
    @NotNull
    private LocalDate birthDate;

    @Schema(description = "성별 - 남자 = MALE / 여자 = FEMALE")
    @NotNull
    private Gender gender;

    @Schema(description = "회원의 현재 위치 - OO시 OOO구까지")
    @NotNull
    private String location;

    @Length(max = 500)
    @NotNull
    private String introduction;

    @Schema(description = "운동 레벨 - 새싹 = BEGINNER / 고수 = ADVANCED")
    @NotNull
    private FitnessLevel fitnessLevel;

    @Schema(description = "운동할 때 주로 누구랑? - 소규모형 = SMALL / 그룹형 = GROUP")
    @NotNull
    private CompanionStyle companionStyle;

    @Schema(description = "운동할 때 나는 평소? - 의욕만렙형 = EAGER / 귀차니즘형 = LAZY")
    @NotNull
    private FitnessEagerness fitnessEagerness;

    @Schema(description = "나의 운동 목적은? - 벌크업 = BULK_UP / 러닝러닝 = RUNNING")
    @NotNull
    private FitnessObjective fitnessObjective;

    @Schema(description = "주로 하고 있는 운동은? - 고강도 운동 위주 = HIGH_STRESS / 기능성 피트니스 위주 = FUNCTIONAL")
    @NotNull
    private FitnessKind fitnessKind;
}
