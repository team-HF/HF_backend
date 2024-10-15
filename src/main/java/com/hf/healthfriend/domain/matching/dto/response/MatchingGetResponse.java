package com.hf.healthfriend.domain.matching.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record MatchingGetResponse (
         String profileImageUrl,
         String nickName,
         long matchingCount,
         String introduction,
         List<String> companionStyle
){
}
