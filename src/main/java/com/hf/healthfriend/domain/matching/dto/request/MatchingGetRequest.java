package com.hf.healthfriend.domain.matching.dto.request;

import com.hf.healthfriend.domain.matching.constant.MatchingSortType;
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
public class MatchingGetRequest {
    /*
    // enum 전환되면 적용 예정
    @Nullable
    private String city; */

    @Nullable
    private List<String> companionStyle;

    @Nullable
    private MatchingSortType matchingSortType;

}
