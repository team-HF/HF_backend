package com.hf.healthfriend.domain.member.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberSearchResponse{
    private String profileImageUrl;
    private String introduction;
    private String nickname;
    private Long wishCount;
    //private Long level;
    private Long matchedCount;
    private String fitnessLevel;
    private String companionStyle;
    private String fitnessEagerness;
    private String fitnessKind;
    private String fitnessObjective;



}
