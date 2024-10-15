package com.hf.healthfriend.domain.review.dto.request;

import com.hf.healthfriend.domain.review.constants.EvaluationType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class ReviewCreationRequestDto {

    @NotNull
    private Long matchingId;

    @NotNull
    private Long reviewerId;

    @NotNull
    private Long revieweeId;

    @Min(1)
    @Max(5)
    private Integer score;

    private List<ReviewEvaluationDto> evaluations;
}
