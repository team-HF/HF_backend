package com.hf.healthfriend.domain.member.dto.request;

import com.hf.healthfriend.domain.member.constant.*;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class MembersRecommendRequest {
    /*
    // enum 전환되면 적용 예정
    @Nullable
    private String city; */

    // 필터링
    @Nullable
    private List<CompanionStyle> companionStyleList;

    @Nullable
    private List<FitnessEagerness> fitnessEagernessList;

    @Nullable
    private List<FitnessKind> fitnessKindList;

    @Nullable
    private List<FitnessObjective> fitnessObjectiveList;

    // 정렬
    @Nullable
    private MemberSortType memberSortType;

}
