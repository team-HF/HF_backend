package com.hf.healthfriend.domain.review.dto.request;

import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class ReviewCreationRequestDto {
    private Long matchingId;
    private Long reviewerId;
    private String description;
    private Integer score;
    private List<ReviewEvaluationDto> evaluations;
}
