package com.hf.healthfriend.domain.member.service;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class TestMemberService {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @DisplayName("createMember - 빠진 데이터 없이 모두 입력")
    @Test
    void createMember_withEveryData() {
        MemberCreationRequestDto requestDto = MemberCreationRequestDto.builder()
                .id("sample@gmail.com")
                .nickname("샘플닉네임")
                .birthDate(LocalDate.now())
                .gender(Gender.MALE)
                .introduction("안녕하세요. 뚱입니다.")
                .fitnessLevel(FitnessLevel.ADVANCED)
                .companionStyle(CompanionStyle.GROUP)
                .fitnessEagerness(FitnessEagerness.LAZY)
                .fitnessObjective(FitnessObjective.RUNNING)
                .fitnessKind(FitnessKind.FUNCTIONAL)
                .build();

        log.info("requestDto={}", requestDto);

        LocalDateTime now = LocalDateTime.now();
        MemberCreationResponseDto responseDto = this.memberService.createMember(requestDto);

        log.info("responseDto={}", responseDto);

        assertThat(responseDto.getMemberId()).isEqualTo("sample@gmail.com");
        assertThat(responseDto.getCreationTime()).isAfterOrEqualTo(now);
        assertThat(responseDto.getRole()).isEqualTo(Role.ROLE_MEMBER.name());
        assertThat(responseDto.getEmail()).isEqualTo("sample@gmail.com");


        assertThat(responseDto.getBirthDate()).isEqualTo(requestDto.getBirthDate());
        assertThat(responseDto.getMemberId()).isEqualTo("sample@gmail.com");
        assertThat(responseDto.getGender()).isEqualTo(Gender.MALE);
        assertThat(responseDto.getNickname()).isEqualTo("샘플닉네임");
        assertThat(responseDto.getIntroduction()).isEqualTo("안녕하세요. 뚱입니다.");
        assertThat(responseDto.getCompanionStyle()).isEqualTo(CompanionStyle.GROUP);
        assertThat(responseDto.getFitnessEagerness()).isEqualTo(FitnessEagerness.LAZY);
        assertThat(responseDto.getFitnessObjective()).isEqualTo(FitnessObjective.RUNNING);
        assertThat(responseDto.getFitnessKind()).isEqualTo(FitnessKind.FUNCTIONAL);

        Member member = this.memberJpaRepository.findById("sample@gmail.com").orElseThrow();

        log.info("Member from repository={}", member);

        assertThat(member).isNotNull();
        assertThat(member.getId()).isEqualTo("sample@gmail.com");
        assertThat(member.getEmail()).isEqualTo("sample@gmail.com");
    }
}