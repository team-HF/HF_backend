package com.hf.healthfriend.domain.member.dto.request;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.repository.dto.MemberUpdateDto;
import com.hf.healthfriend.domain.spec.dto.request.SpecUpdateRequestDto;
import com.hf.healthfriend.global.file.image.ImageExtension;
import com.hf.healthfriend.global.util.mapping.BeanMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Setter
@Getter
@ToString
@BeanMapping(MemberUpdateDto.class)
public class MemberUpdateRequestDto {

    @Schema(description = "회원이 현재 위치하고 있는 시/도 id")
    @Length(min = 2, max = 2)
    private String cd1;

    @Schema(description = "회원이 현재 위치하고 있는 구 id")
    @Length(min = 3, max = 3)
    private String cd2;

    @Schema(description = "회원이 현재 위치하고 있는 동 id")
    @Length(min = 3, max = 3)
    private String cd3;

    @Length(max = 500)
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

    @Schema(description = "회원 프로필 사진 파일의 확장자. null일 경우, 기본 프로필 사진 사용")
    private ImageExtension profileImageFileExtension;

    @Schema(description = "수상 및 경력")
    private List<SpecUpdateRequestDto> specUpdate;
}
