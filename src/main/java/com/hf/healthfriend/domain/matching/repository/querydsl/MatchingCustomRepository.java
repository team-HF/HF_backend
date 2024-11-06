package com.hf.healthfriend.domain.matching.repository.querydsl;

import com.hf.healthfriend.domain.matching.constant.MatchingFetchType;
import com.hf.healthfriend.domain.matching.constant.MatchingStatusCondition;
import com.hf.healthfriend.domain.matching.dto.response.MatchingListResponseDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MatchingCustomRepository {


    Page<MatchingListResponseDto> findByMemberIdWithConditions(@NotNull Long memberId,
                                                               @NotNull MatchingFetchType matchingFetchType,
                                                               @NotNull MatchingStatusCondition matchingStatusCondition,
                                                               @NotNull Pageable page);
}
