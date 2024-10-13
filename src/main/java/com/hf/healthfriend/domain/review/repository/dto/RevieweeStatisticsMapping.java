package com.hf.healthfriend.domain.review.repository.dto;

import com.hf.healthfriend.domain.review.constants.EvaluationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class RevieweeStatisticsMapping {
    private EvaluationType evaluationType;
    private int evaluationDetailId;
    private long evaluationDetailCount;
}
