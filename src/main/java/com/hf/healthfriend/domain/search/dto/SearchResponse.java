package com.hf.healthfriend.domain.search.dto;

import com.hf.healthfriend.domain.member.dto.response.MemberSearchResponse;
import com.hf.healthfriend.domain.post.dto.response.PostListObject;
import java.util.List;
import lombok.Builder;

@Builder
public record SearchResponse(
        List<PostListObject> postList,
        List<MemberSearchResponse> profileList
) {
}
