package com.hf.healthfriend.domain.spec.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hf.healthfriend.domain.spec.entity.Spec;
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
    private LocalDate startDate;

    @PastOrPresent
    private LocalDate endDate;

    private boolean isCurrent;

    @NotEmpty
    private String title;

    @NotNull
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
