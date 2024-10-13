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

    @AssertTrue
    private boolean isDuplicateEvaluationPresent() {
        // TODO: iteration 때문에 성능상 약간 좀 안 좋을 수 있음 - 이걸 DB에서 검증하도록 하는 게 나을까?
        Map<EvaluationType, Set<Integer>> evaluationIdsByEvaluationType = new HashMap<>();
        for (ReviewEvaluationDto evaluationDto : this.evaluations) {
            EvaluationType evaluationType = evaluationDto.getEvaluationType();
            Integer evaluationId = evaluationDto.getEvaluationDetailId();
            if (evaluationIdsByEvaluationType.containsKey(evaluationType)) {
                Set<Integer> ids = evaluationIdsByEvaluationType.get(evaluationType);
                if (ids.contains(evaluationId)) {
                    return false;
                }
                ids.add(evaluationId);
            } else {
                Set<Integer> idSet = new HashSet<>();
                idSet.add(evaluationId);
                evaluationIdsByEvaluationType.put(evaluationType, idSet);
            }
        }
        return true;
    }
}
