package com.hf.healthfriend.domain.member.service;

import com.hf.healthfriend.auth.itself.dto.request.SignUpRequestDto;
import com.hf.healthfriend.auth.jwt.service.JwtService;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.entity.Role;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.profile.entity.Profile;
import com.hf.healthfriend.domain.profile.repository.ProfileRepository;
import com.hf.healthfriend.global.exception.custom.MemberException;
import com.hf.healthfriend.global.exception.errorcode.CommonErrorCode;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Member saveOrUpdate(Member member){
        return memberRepository.findByEmail(member.getEmail())
                .orElseGet(() -> memberRepository.save(member));
    }

    public void saveRefresh(String email, String refreshToken) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(CommonErrorCode.NO_EXIST_EMAIL_MEMBER_EXCEPTION));

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
//        if(memberRepository.existsByEmail(signUpRequestDto.getEmail())){
//            throw new MemberException(CommonErrorCode.CONFLICT_EMAIL_MEMBER_EXCEPTION);
//        }
//
//        Profile profile = Profile.builder()
//                .nickName(signUpRequestDto.getNickName())
//                .longitude(signUpRequestDto.getLongitude())
//                .latitude(signUpRequestDto.getLatitude())
//                .nicknameChangeCount(0L)
//                .build();
//
//        Member member = Member.builder()
//                .role(Role.ROLE_USER)
//                .email(signUpRequestDto.getEmail())
//                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
//                .build();
//
//        profileRepository.save(profile);
//        memberRepository.save(member);
//
//        return ApiBasicResponse.builder()
//                .status(true)
//                .code(HttpStatus.OK.value())
//                .message("회원가입 성공!")
//                .build();
        throw new UnsupportedOperationException();
    }

    public ResponseEntity<ApiBasicResponse> reissue(String refreshToken){
//        if(refreshToken == null){
//            throw new MemberException(CommonErrorCode.NO_EXIST_REFRESH_TOKEN_COOKIE_EXCEPTION);
//        }
//
//        jwtService.validateToken(refreshToken);
//        Member member = memberRepository.findByRefreshToken(refreshToken)
//                .orElseThrow(() -> new MemberException(CommonErrorCode.REFRESH_TOKEN_MISMATCH_EXCEPTION));
//
//        String accessToken = jwtService.createAccessToken(member.getEmail(), Role.ROLE_USER.name());
//
//        return ResponseEntity.ok()
//                .header("Authorization", accessToken)
//                .body(ApiBasicResponse.builder()
//                        .status(true)
//                        .code(200)
//                        .message("Access Token 발급 성공! Authorization 헤더를 확인하세요")
//                        .build());
        throw new UnsupportedOperationException();
    }
}
