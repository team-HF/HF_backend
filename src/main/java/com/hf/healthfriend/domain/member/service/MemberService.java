package com.hf.healthfriend.domain.member.service;

import com.hf.healthfriend.auth.itself.dto.request.SignUpRequestDto;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.entity.Role;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.global.exception.custom.MemberException;
import com.hf.healthfriend.global.exception.errorcode.CommonErrorCode;
import com.hf.healthfriend.global.exception.errorcode.ErrorCode;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member saveOrUpdate(Member member){
        return memberRepository.findByEmail(member.getEmail())
                .orElseGet(() -> memberRepository.save(member));
    }

    public void saveRefresh(String email, String refreshToken) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(CommonErrorCode.NO_EXIST_EMAIL_MEMBER_EXCEPTION));

        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);
    }

    public Member findMemberByEmail(String email){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(CommonErrorCode.NO_EXIST_EMAIL_MEMBER_EXCEPTION));

        return member;
    }


    public ApiBasicResponse checkDuplicateEmail(String email) {
        boolean exists = memberRepository.existsByEmail(email);

        if(exists){
            return ApiBasicResponse.builder()
                    .status(true)
                    .code(HttpStatus.CONFLICT.value())
                    .message("중복되는 이메일이 존재합니다")
                    .build();
        }

        return ApiBasicResponse.builder()
                .status(true)
                .code(HttpStatus.OK.value())
                .message("해당 이메일을 사용할 수 있습니다!")
                .build();
    }

    public ApiBasicResponse signUp(SignUpRequestDto signUpRequestDto) {
        if(memberRepository.existsByEmail(signUpRequestDto.getEmail())){
            throw new MemberException(CommonErrorCode.CONFLICT_EMAIL_MEMBER_EXCEPTION);
        }

        Member member = Member.builder()
                .role(Role.ROLE_USER)
                .nickName(signUpRequestDto.getNickName())
                .email(signUpRequestDto.getEmail())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .latitude(signUpRequestDto.getLatitude())
                .longitude(signUpRequestDto.getLongitude())
                .build();

        memberRepository.save(member);

        return ApiBasicResponse.builder()
                .status(true)
                .code(HttpStatus.OK.value())
                .message("회원가입 성공!")
                .build();
    }
}
