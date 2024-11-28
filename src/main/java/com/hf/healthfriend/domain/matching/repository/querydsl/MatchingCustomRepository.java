package com.hf.healthfriend.domain.matching.repository.querydsl;

import com.hf.healthfriend.domain.matching.constant.MatchingFetchType;
import com.hf.healthfriend.domain.matching.constant.MatchingStatusCondition;
import com.hf.healthfriend.domain.matching.dto.response.MatchingListResponseDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface MatchingCustomRepository {


    Page<MatchingListResponseDto> findByMemberIdWithConditions(@NotNull Long memberId,
                                                               @NotNull MatchingFetchType matchingFetchType,
                                                               @NotNull MatchingStatusCondition matchingStatusCondition,
                                                               @NotNull Pageable page);

    /**
     * <p>중복되는 매칭이 있는지 체크하는 메소드.
     * <p>매칭 중복 여부:
     *
     * <p>1. 주어진 날짜에 신청된 매칭이 있는 경우 중복
     * <p>2. 해당 회원이 이미 진행 중인 매칭이 있는 경우 중복
     *
     * @param memberId 체크하려는 회원의 ID
     * @param date 체크하려는 날짜
     * @return 중복이 있으면 true, 그렇지 않으면 false를 반환
     */
    boolean existsDuplicateMatchingRequest(Long memberId, LocalDate date);
}
