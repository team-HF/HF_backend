package com.hf.healthfriend.domain.spec.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hf.healthfriend.domain.spec.entity.Spec;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class SpecDto {

    @NotNull
    @PastOrPresent
    @Schema(description = "경력 시작 날짜. 수상 이력일 경우, 수상 날짜")
    private LocalDate startDate;

    @PastOrPresent
    @Schema(description = "경력 종료 날짜. 수상 이력일 경우, null")
    private LocalDate endDate;

    @Schema(description = "경력이 현재 진행 중인지 여부. 수상 이력일 경우 null. endDate가 null이 아닐 경우, isCurrent는 true일 수 없음")
    private boolean isCurrent;

    @NotEmpty
    @Schema(description = "경력 혹은 수상 이력 타이틀. null이거나 빈 문자열일 수 없음")
    private String title;

    @NotNull
    @Schema(description = "경력 혹은 수상 이력 내용. 빈 문자열일 수 있지만 null일 수는 없음")
    private String description;

    public static SpecDto of(Spec entity) {
        return new SpecDto(
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isCurrent(),
                entity.getTitle(),
                entity.getDescription()
        );
    }

    @AssertTrue(message = "endDate가 startDate 이전임")
    private boolean isStartDateBeforeEndDate() {
        return this.endDate == null
                || this.startDate.isBefore(this.endDate)
                || this.startDate.equals(this.endDate);
    }

    @AssertFalse(message = "endDate가 null이 아닌데 isCurrent가 true임")
    private boolean isEndDateNotNullWhenIsCurrentIsTrue() {
        return this.endDate != null && this.isCurrent;
    }

    @JsonIgnore
    public boolean isCurrent() {
        return this.isCurrent;
    }

    // For Jackson mapping
    @JsonProperty("isCurrent")
    protected boolean getIsCurrent() {
        return this.isCurrent;
    }
}
