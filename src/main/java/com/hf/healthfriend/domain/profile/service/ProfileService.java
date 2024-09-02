package com.hf.healthfriend.domain.profile.service;

import com.hf.healthfriend.auth.principal.PrincipalDetails;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.profile.dto.request.FitnessProfileDto;
import com.hf.healthfriend.domain.profile.dto.request.NicknameDto;
import com.hf.healthfriend.domain.profile.entity.Profile;
import com.hf.healthfriend.domain.profile.repository.ProfileRepository;
import com.hf.healthfriend.global.exception.custom.MemberException;
import com.hf.healthfriend.global.exception.errorcode.CommonErrorCode;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final MemberRepository memberRepository;

    public ApiBasicResponse setNickname(NicknameDto nicknameDto, PrincipalDetails principalDetails){
//        Member member = memberRepository.findByEmail(principalDetails.getEmail())
//                .orElseThrow(() -> new MemberException(CommonErrorCode.NO_EXIST_EMAIL_MEMBER_EXCEPTION));
//
//        Profile profile = member.getProfile();
//        if(profile.getNicknameChangeCount() > 5){
//            throw new MemberException(CommonErrorCode.EXCEEDED_NICKNAME_CHANGE_LIMIT);
//        }
//
//        profile.updateNickName(nicknameDto);
//
//        memberRepository.save(member);
//
//        return ApiBasicResponse.builder()
//                .status(true)
//                .code(200)
//                .message("닉네임 설정 성공!")
//                .build();
        throw new UnsupportedOperationException();
    }

    public ApiBasicResponse setProfileLevel(FitnessProfileDto fitnessProfileDto, PrincipalDetails principalDetails){
//        Member member = memberRepository.findByEmail(principalDetails.getEmail())
//                .orElseThrow(() -> new MemberException(CommonErrorCode.NO_EXIST_EMAIL_MEMBER_EXCEPTION));
//
//        Profile profile = member.getProfile();
//        profile.updateFitnessLevel(fitnessProfileDto);
//        profile.updateFitnessStyle(fitnessProfileDto);
//        memberRepository.save(member);
//
//        return ApiBasicResponse.builder()
//                .status(true)
//                .code(200)
//                .message("운동 프로필 기본 설정 성공!")
//                .build();
        throw new UnsupportedOperationException();
    }
}
