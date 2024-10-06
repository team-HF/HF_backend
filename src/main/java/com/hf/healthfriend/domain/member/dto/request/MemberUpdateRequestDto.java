package com.hf.healthfriend.domain.member.dto.request;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.repository.dto.MemberUpdateDto;
import com.hf.healthfriend.global.util.mapping.BeanMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Setter
@Getter
@ToString
@BeanMapping(MemberUpdateDto.class)
public class MemberUpdateRequestDto {
    private MultipartFile profileImage;

    @Schema(description = "회원이 현재 위치한 시")
    private String city;

    @Schema(description = "회원이 현재 위치한 구")
    private String district;

    @Length(max = 500)
    private String introduction;

    private FitnessLevel fitnessLevel;
    private CompanionStyle companionStyle;
    private FitnessEagerness fitnessEagerness;
    private FitnessObjective fitnessObjective;
    private FitnessKind fitnessKind;
}
