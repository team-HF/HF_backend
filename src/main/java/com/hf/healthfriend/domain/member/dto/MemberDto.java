package com.hf.healthfriend.domain.member.dto;

import com.hf.healthfriend.domain.member.constant.Role;
import com.hf.healthfriend.domain.member.entity.Member;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class MemberDto {
    private String memberId;
    private Role role;
    private String email;
    private LocalDateTime creationTime;

    // TODO: Profile도 받아야 하는가?

    public static MemberDto of(Member member) {
        MemberDto dto = new MemberDto();
        BeanUtils.copyProperties(member, dto);
        return dto;
    }
}
