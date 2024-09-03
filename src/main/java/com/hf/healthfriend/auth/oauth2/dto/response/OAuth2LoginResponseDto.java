package com.hf.healthfriend.auth.oauth2.dto.response;

import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class OAuth2LoginResponseDto {
    MemberCreationResponseDto memberCreationInfo;
    GrantedTokenInfo grantedTokenInfo;
}
