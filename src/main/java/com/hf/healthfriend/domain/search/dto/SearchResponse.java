package com.hf.healthfriend.domain.search.dto;

import com.hf.healthfriend.domain.member.dto.response.MemberSearchResponse;
import com.hf.healthfriend.domain.post.dto.response.PostSearchResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record SearchResponse(
        PostSearchResponse postList,
        Long postListSize,
        MemberSearchResponse profileList,
        Long profileListSize,
        List<String> recentSearchList
) {
}
