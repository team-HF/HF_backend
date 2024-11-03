package com.hf.healthfriend.domain.review.repository.dto;

import com.hf.healthfriend.domain.review.constants.EvaluationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class RevieweeStatisticsQueryResultDto {
    private EvaluationType evaluationType;
    private Integer evaluationDetailId;
    private Long evaluationDetailCount;
}
