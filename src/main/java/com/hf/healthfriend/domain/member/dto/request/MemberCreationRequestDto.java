package com.hf.healthfriend.domain.member.dto.request;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class MemberCreationRequestDto {
    private String id;
    private String email;
    private String password;
}
