package com.hf.healthfriend.domain.review.repository.dto;

import com.hf.healthfriend.domain.review.constants.EvaluationType;

public interface RevieweeStatisticsMapping {
    EvaluationType getEvaluationType();
    int getEvaluationDetailId();
    long getEvaluationDetailCount();
}
