package com.hf.healthfriend.domain.member.dto.request;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.repository.dto.MemberUpdateDto;
import com.hf.healthfriend.domain.spec.dto.request.SpecUpdateRequestDto;
import com.hf.healthfriend.global.util.mapping.BeanMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
    private MultipartFile profileImage;

    @Schema(description = "회원이 현재 위치하고 있는 시/도 id")
    @NotNull
    @Length(min = 2, max = 2)
    private String cd1;

    @Schema(description = "회원이 현재 위치하고 있는 구 id")
    @NotNull
    @Length(min = 3, max = 3)
    private String cd2;

    @Schema(description = "회원이 현재 위치하고 있는 동 id")
    @NotNull
    @Length(min = 3, max = 3)
    private String cd3;

    @Length(max = 500)
    private String introduction;

    private FitnessLevel fitnessLevel;
    private CompanionStyle companionStyle;
    private FitnessEagerness fitnessEagerness;
    private FitnessObjective fitnessObjective;
    private FitnessKind fitnessKind;

    @Schema(description = "회원 프로필 이미지 등록 여부")
    private boolean profileImagePresent;

    @Schema(description = "수상 및 경력")
    @NotNull
    private List<SpecUpdateRequestDto> specUpdate;
}
