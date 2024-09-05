package com.hf.healthfriend.domain.member.dto.response;

import com.hf.healthfriend.domain.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class MemberCreationResponseDto {
    private final boolean creationOccurred;
    private final String memberId;
    private final String email;
    private final String role;
    private final LocalDateTime creationTime;

    public static MemberCreationResponseDto of(Member member) {
        return MemberCreationResponseDto.builder()
                .creationOccurred(true)
                .memberId(member.getId())
                .email(member.getEmail())
                .role(member.getRole().name())
                .creationTime(member.getCreationTime())
                .build();
    }

    public static MemberCreationResponseDto notCreated(String memberId) {
        return MemberCreationResponseDto.builder()
                .creationOccurred(false)
                .memberId(memberId)
                .build();
    }
}
