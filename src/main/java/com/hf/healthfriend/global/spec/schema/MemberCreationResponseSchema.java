package com.hf.healthfriend.global.spec.schema;

import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 생성 응답")
public class MemberCreationResponseSchema extends ApiBasicResponse<MemberCreationResponseDto> {

    @Schema(description = "응답코드", defaultValue = "201")
    @Override
    public int getStatusCode() {
        return super.getStatusCode();
    }

    @Schema(description = "응답코드 시리즈", defaultValue = "2")
    @Override
    public int getStatusCodeSeries() {
        return super.getStatusCodeSeries();
    }

    @Schema(description = "메세지는 null", defaultValue = "null")
    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public MemberCreationResponseDto getContent() {
        return super.getContent();
    }
}
