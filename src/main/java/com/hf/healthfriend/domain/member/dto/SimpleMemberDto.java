package com.hf.healthfriend.domain.member.dto;

import com.hf.healthfriend.domain.member.entity.Member;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class SimpleMemberDto {
    private Long memberId;
    private String loginId;
    private String email;
    private String name;
    private String nickname;

    public static SimpleMemberDto of(Member entity) {
        return SimpleMemberDto.builder()
                .memberId(entity.getId())
                .loginId(entity.getLoginId())
                .email(entity.getEmail())
                .name(entity.getName())
                .nickname(entity.getNickname())
                .build();
    }
}
