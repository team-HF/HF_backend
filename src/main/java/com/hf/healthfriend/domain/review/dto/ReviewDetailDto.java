package com.hf.healthfriend.domain.review.dto;

import com.hf.healthfriend.domain.review.constants.EvaluationType;
import lombok.*;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class ReviewDetailDto {
    private EvaluationType evaluationType;
    private Map<Integer, Integer> reviewDetailCount;
}
