package com.hf.healthfriend.domain.review.dto.request;

import com.hf.healthfriend.domain.review.constants.EvaluationType;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class ReviewEvaluationDto {
    private EvaluationType evaluationType;
    private Integer evaluationDetailId;
}
