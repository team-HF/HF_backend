package com.hf.healthfriend.domain.spec.dto.request;

import com.hf.healthfriend.domain.spec.constants.SpecUpdateType;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class SpecUpdateRequestDto {
    private SpecUpdateType specUpdateType;

    @Schema(description = "경력의 ID. 수정 혹은 삭제 시, null이면 안 됨")
    private Long specId;

    @Schema(description = "경력 및 수상 사항이 담긴 DTO")
    private SpecDto spec;


    @AssertTrue(message = "specUpdateType이 INSERT가 아닐 경우 specId는 Not Null")
    private boolean isSpecIdNotNullWhenSpecUpdateTimeIsNotINSERT() {
        System.out.println(this.specUpdateType);
        System.out.println(this.specId);
        return this.specUpdateType == SpecUpdateType.INSERT
                || this.specId != null;
    }

    @AssertTrue(message = "specUpdateType이 Delete가 아닐 경우 spec은 Not Null")
    private boolean isSpecNotNullWhenspecUpdateTimeIsNotDELETE() {
        return this.specUpdateType == SpecUpdateType.DELETE
                || this.spec != null;
    }
}
