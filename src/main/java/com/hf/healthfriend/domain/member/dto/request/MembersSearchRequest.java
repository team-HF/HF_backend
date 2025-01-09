package com.hf.healthfriend.domain.member.dto.request;

import com.hf.healthfriend.domain.member.constant.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class MembersSearchRequest {

    // 필터링
    private String cd1;

    private String cd2;

    private String cd3;

    private String fitnessLevel;

    private String companionStyle;

    private String fitnessEagerness;

    private String fitnessKind;

    private String fitnessObjective;

    // 정렬
    private MemberSortType memberSortType;

}
