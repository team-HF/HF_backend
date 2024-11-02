package com.hf.healthfriend.domain.member.repository.dto;

import com.hf.healthfriend.domain.review.constants.EvaluationType;

import java.time.LocalDate;

//public record ProfileResultDto(
//        Long memberId,
//        Double reviewScore,
//        // TODO: 찜하기 기능이 머지되면 추가
////        Long wishedCount,
//        String introduction,
//        Long specId,
//        LocalDate startDate,
//        LocalDate endDate,
//        boolean isCurrent,
//        String title,
//        String description,
//        EvaluationType evaluationType,
//        Integer evaluationDetailId,
//        Long evaluationDetailCount
//) {
//}

public interface ProfileResultMapping {

    Long getMemberId();
    Double getReviewScore();
    // TODO: 찜하기 기능이 머지되면 추가
//        Long getWishedCount();
    String getIntroduction();
    Long getSpecId();
    LocalDate getStartDate();
    LocalDate getEndDate();
    boolean isCurrent();
    String getTitle();
    String getDescription();
    EvaluationType getEvaluationType();
    Integer getEvaluationDetailId();
    Long getEvaluationDetailCount();
}