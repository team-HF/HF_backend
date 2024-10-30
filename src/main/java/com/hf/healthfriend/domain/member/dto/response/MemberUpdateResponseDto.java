package com.hf.healthfriend.domain.member.dto.response;

import com.hf.healthfriend.domain.member.constant.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(toBuilder = true)
public record MemberUpdateResponseDto(

        @Schema(description = "프로필 이미지를 업로드할 URL. 이 URL로 PUT Method를 통해 이미지 binary data를 전송하면 된다. " +
                "이 URL로 이미지 업로드 요청을 보낼 때 Content-Type은 image/png, image/jpeg 등")
        String profileImageUploadUrl,
        String cd1,
        String cd2,
        String cd3,
        String introduction,
        FitnessLevel fitnessLevel,
        CompanionStyle companionStyle,
        FitnessEagerness fitnessEagerness,
        FitnessObjective fitnessObjective,
        FitnessKind fitnessKind
) {
}
