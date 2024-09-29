package com.hf.healthfriend.domain.spec.dto;

import com.hf.healthfriend.domain.spec.entity.Spec;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
}
