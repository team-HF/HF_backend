package com.hf.healthfriend.domain.review.dto.request;

import com.hf.healthfriend.domain.review.constants.EvaluationType;
import lombok.*;

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
    private EvaluationType evaluationType;
}
