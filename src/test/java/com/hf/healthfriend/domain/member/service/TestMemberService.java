package com.hf.healthfriend.domain.member.service;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.request.MemberUpdateRequestDto;
import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.FitnessLevelUpdateException;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberJpaRepository;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Slf4j
@SpringBootTest
@ActiveProfiles({
        "local-dev",
        "secret",
        "constants",
        "priv"
})
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

        assertThat(responseDto.getLoginId()).isEqualTo("sample@gmail.com");
        assertThat(responseDto.getCreationTime()).isAfterOrEqualTo(now);
        assertThat(responseDto.getRole()).isEqualTo(Role.ROLE_MEMBER.name());
        assertThat(responseDto.getEmail()).isEqualTo("sample@gmail.com");


        assertThat(responseDto.getBirthDate()).isEqualTo(requestDto.getBirthDate());
        assertThat(responseDto.getLoginId()).isEqualTo("sample@gmail.com");
        assertThat(responseDto.getGender()).isEqualTo(Gender.MALE);
        assertThat(responseDto.getNickname()).isEqualTo("샘플닉네임");
        assertThat(responseDto.getIntroduction()).isEqualTo("안녕하세요. 뚱입니다.");
        assertThat(responseDto.getCompanionStyle()).isEqualTo(CompanionStyle.GROUP);
        assertThat(responseDto.getFitnessEagerness()).isEqualTo(FitnessEagerness.LAZY);
        assertThat(responseDto.getFitnessObjective()).isEqualTo(FitnessObjective.RUNNING);
        assertThat(responseDto.getFitnessKind()).isEqualTo(FitnessKind.FUNCTIONAL);

        Member member = this.memberJpaRepository.findByEmail("sample@gmail.com").orElseThrow();

        log.info("Member from repository={}", member);

        assertThat(member).isNotNull();
        assertThat(member.getLoginId()).isEqualTo("sample@gmail.com");
        assertThat(member.getEmail()).isEqualTo("sample@gmail.com");
    }

    private List<SpecDto> getSampleSpecs() {
        return List.of(
                new SpecDto(
                        LocalDate.of(2014, 1, 2),
                        LocalDate.of(2017, 2, 15),
                        false,
                        "sample-title",
                        "sample-desc"
                ),
                new SpecDto(
                        LocalDate.of(2015, 7, 2),
                        null,
                        true,
                        "sample-title",
                        "sample-desc"
                ),
                new SpecDto(
                        LocalDate.of(2015, 7, 2),
                        null,
                        false,
                        "sample-title",
                        "sample-desc"
                )
        );
    }

    @DisplayName("createMember - id가 빠지면 안 됨")
    @Test
    void createMember_withoutId() {
        MemberCreationRequestDto requestDto = MemberCreationRequestDto.builder()
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

        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> this.memberService.createMember(requestDto));
    }

    static Stream<MemberCreationRequestDto> createMember_omittedData() {
        return Stream.of(
                MemberCreationRequestDto.builder()
                        .id("sample@gmail.com")
                        .build(),
                MemberCreationRequestDto.builder()
                        .id("elpmas@khu.ac.kr")
                        .nickname("샘플닉네임")
                        .birthDate(LocalDate.now())
                        .gender(Gender.FEMALE)
                        .build(),
                MemberCreationRequestDto.builder()
                        .id("asdf@naver.com")
                        .fitnessEagerness(FitnessEagerness.LAZY)
                        .build()
        );
    }

    @DisplayName("createMember - 누락된 데이터가 있으면 삽입 실패 - DataIntegrityViolationException")
    @MethodSource
    @ParameterizedTest
    void createMember_omittedData(MemberCreationRequestDto testcase) {
        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> this.memberService.createMember(testcase));
    }

    @DisplayName("isMemberExists - exists member")
    @Test
    void isMemberExists_returnTrue() {
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

        this.memberService.createMember(requestDto);

        boolean memberExists = this.memberService.isMemberOfEmailExists("sample@gmail.com");
        assertThat(memberExists).isTrue();
    }

    @DisplayName("isMemberExists - not exists member")
    @Test
    void isMemberExists_returnFalse() {
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

        this.memberService.createMember(requestDto);

        boolean memberExists = this.memberService.isMemberOfEmailExists("no@such.member");
        assertThat(memberExists).isFalse();
    }

    @DisplayName("findMember - Member 제대로 찾음")
    @Test
    void findMember_success() {
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

        LocalDateTime beforeExecution = LocalDateTime.now();
        this.memberService.createMember(requestDto);

        MemberDto result = this.memberService.findMemberByEmail("sample@gmail.com");

        log.info("result={}", result);

        assertThat(result.getLoginId()).isEqualTo(requestDto.getId());
        assertThat(result.getNickname()).isEqualTo(requestDto.getNickname());
        assertThat(result.getCreationTime()).isAfterOrEqualTo(beforeExecution);
        assertThat(result.getCreationTime()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(result.getBirthDate()).isEqualTo(requestDto.getBirthDate());
        assertThat(result.getGender()).isEqualTo(requestDto.getGender());
        assertThat(result.getIntroduction()).isEqualTo(requestDto.getIntroduction());
        assertThat(result.getFitnessLevel()).isEqualTo(requestDto.getFitnessLevel());
        assertThat(result.getCompanionStyle()).isEqualTo(requestDto.getCompanionStyle());
        assertThat(result.getFitnessEagerness()).isEqualTo(requestDto.getFitnessEagerness());
        assertThat(result.getFitnessObjective()).isEqualTo(requestDto.getFitnessObjective());
        assertThat(result.getFitnessKind()).isEqualTo(requestDto.getFitnessKind());
    }

    @DisplayName("findMember - 찾으려는 Member가 없을 경우 MemberNotFoundException 발생")
    @Test
    void findMember_fail_MemberNotFoundException() {
        assertThatExceptionOfType(MemberNotFoundException.class)
                .isThrownBy(() -> this.memberService.findMemberByEmail("no@such.member"));
    }

    static Stream<MemberUpdateRequestDto> updateMember_success() {
        return Stream.of(
                MemberUpdateRequestDto.builder()
                        .location("서울시 구로구")
                        .build(),
                MemberUpdateRequestDto.builder()
                        .introduction("그하하하하")
                        .build(),
                MemberUpdateRequestDto.builder()
                        .introduction("수정된 한 줄 소개")
                        .companionStyle(CompanionStyle.GROUP)
                        .build(),
                MemberUpdateRequestDto.builder()
                        .build(),
                MemberUpdateRequestDto.builder()
                        .introduction("바뀐 소개")
                        .companionStyle(CompanionStyle.SMALL)
                        .fitnessEagerness(FitnessEagerness.EAGER)
                        .fitnessObjective(FitnessObjective.BULK_UP)
                        .fitnessKind(FitnessKind.HIGH_STRESS)
                        .build(),
                MemberUpdateRequestDto.builder()
                        .introduction("안녕하세요. 뚱입니다.")
                        .companionStyle(CompanionStyle.GROUP)
                        .fitnessEagerness(FitnessEagerness.LAZY)
                        .fitnessObjective(FitnessObjective.RUNNING)
                        .fitnessKind(FitnessKind.FUNCTIONAL)
                        .build()
        );
    }

    @DisplayName("updateMember - 제대로 수정됨")
    @MethodSource
    @ParameterizedTest
    void updateMember_success(MemberUpdateRequestDto updateDto) {
        Member sampleMember = SampleEntityGenerator.generateSampleMember("sample@gmail.com");
        this.memberJpaRepository.save(sampleMember);

        Map<String, Object> updateValueMap = Arrays.stream(MemberUpdateRequestDto.class.getDeclaredMethods())
                .filter((m) -> m.getName().startsWith("get"))
                .filter((m) -> {
                    try {
                        return m.invoke(updateDto) != null;
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .collect(Collectors.toMap((m) -> getterToFieldName(m.getName()), (m) -> {
                    try {
                        return m.invoke(updateDto);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                        throw new RuntimeException();
                    }
                }));

        log.info("updateValueMap={}", updateValueMap);

        MemberDto beforeUpdate = this.memberService.findMemberByEmail("sample@gmail.com");

        this.memberService.updateMember(sampleMember.getId(), updateDto);

        MemberDto updatedMember = this.memberService.findMemberByEmail("sample@gmail.com");

        log.info("updateDto={}", updateDto);
        log.info("beforeUpdate={}", beforeUpdate);
        log.info("updatedMember={}", updatedMember);

        for (Method getter : Arrays.stream(MemberDto.class.getDeclaredMethods()).filter((m) -> m.getName().startsWith("get")).toArray(Method[]::new)) {
            String fieldName = getterToFieldName(getter.getName());
            try {
                Object resultValue = getter.invoke(updatedMember);
                if (updateValueMap.containsKey(fieldName)) {
                    Object update = updateValueMap.get(fieldName);
                    assertThat(resultValue).isEqualTo(update);
                } else {
                    Object beforeValue = getter.invoke(beforeUpdate);
                    assertThat(resultValue).isEqualTo(beforeValue);
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private String getterToFieldName(String getterName) {
        if (!getterName.startsWith("get")) {
            return getterName;
        }
        getterName = getterName.substring(3);
        return Character.toLowerCase(getterName.charAt(0)) + getterName.substring(1);
    }

    @DisplayName("updateMember - 운동 레벨을 \"고수\"에서 \"새싹\"으로 바꿀 경우 예외 발생 - FitnessLevelUpdateException")
    @Test
    void updateMember_updateFitnessLevelNotAllowed_FitnessLevelUpdateException() {
        Member sampleMember = SampleEntityGenerator.generateSampleMember("sample@gmail.com");
        sampleMember.setFitnessLevel(FitnessLevel.ADVANCED);
        this.memberJpaRepository.save(sampleMember);

        MemberUpdateRequestDto updateDto = MemberUpdateRequestDto.builder()
                .fitnessLevel(FitnessLevel.BEGINNER)
                .build();

        assertThatExceptionOfType(FitnessLevelUpdateException.class)
                .isThrownBy(() -> this.memberService.updateMember(sampleMember.getId(), updateDto));
    }

    @DisplayName("updateMember - 없는 회원일 경우 MemberNotFoundException 발생")
    @Test
    void updateMember_MemberNotFoundException() {
        MemberUpdateRequestDto updateDto = MemberUpdateRequestDto.builder()
                .build();
        assertThatExceptionOfType(MemberNotFoundException.class)
                .isThrownBy(() -> this.memberService.updateMember(1521L, updateDto));
    }
}