package com.hf.healthfriend.domain.member.controller;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.request.MemberUpdateRequestDto;
import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.domain.member.dto.response.MemberUpdateResponseDto;
import com.hf.healthfriend.domain.member.exception.DuplicateMemberCreationException;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockBean(JpaMetamodelMappingContext.class)
@MockBean(MemberRepository.class)
class MemberControllerMockMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @BeforeEach
    void beforeEach() {
        MemberCreationRequestDto given = MemberCreationRequestDto.builder()
                .id("new@gmail.com")
                .nickname("새로운인간")
                .name("김샘플")
                .birthDate(LocalDate.of(1997, 9, 16))
                .gender(Gender.MALE)
                .cd1("01")
                .cd2("110")
                .cd3("112")
                .introduction("안녕하세요")
                .fitnessLevel(FitnessLevel.BEGINNER)
                .companionStyle(CompanionStyle.GROUP)
                .fitnessEagerness(FitnessEagerness.EAGER)
                .fitnessObjective(FitnessObjective.BULK_UP)
                .fitnessKind(FitnessKind.FUNCTIONAL)
                .build();

        LocalDateTime now = LocalDateTime.now();
        when(this.memberService.createMember(given))
                .thenReturn(MemberCreationResponseDto.builder()
                        .memberId(1000L)
                        .loginId(given.getId())
                        .email(given.getId())
                        .role(Role.ROLE_MEMBER.name())
                        .creationTime(now)
                        .nickname(given.getNickname())
                        .birthDate(given.getBirthDate())
                        .gender(given.getGender())
                        .introduction(given.getIntroduction())
                        .fitnessLevel(given.getFitnessLevel())
                        .companionStyle(given.getCompanionStyle())
                        .fitnessObjective(given.getFitnessObjective())
                        .fitnessKind(given.getFitnessKind())
                        .build());

        MemberCreationRequestDto duplicateCreation = MemberCreationRequestDto.builder()
                .id("duplicate@gmail.com")
                .nickname("샘플닉네임")
                .name("김샘플")
                .birthDate(LocalDate.of(1997, 9, 16))
                .gender(Gender.MALE)
                .cd1("01")
                .cd2("110")
                .cd3("112")
                .introduction("안녕하세요")
                .fitnessLevel(FitnessLevel.BEGINNER)
                .companionStyle(CompanionStyle.GROUP)
                .fitnessEagerness(FitnessEagerness.EAGER)
                .fitnessObjective(FitnessObjective.BULK_UP)
                .fitnessKind(FitnessKind.FUNCTIONAL)
                .build();

        when(this.memberService.createMember(duplicateCreation))
                .thenThrow(new DuplicateMemberCreationException(duplicateCreation.getId()));

        when(this.memberService.findMember(10050L)).thenReturn(MemberDto.builder()
                .memberId(10050L)
                .loginId("sample@gmail.com")
                .email("sample@gmail.com")
                .role(Role.ROLE_MEMBER)
                .nickname("샘플닉네임")
                .name("김샘플")
                .birthDate(LocalDate.of(1997, 9, 16))
                .gender(Gender.MALE)
                .cd1("01")
                .cd2("110")
                .cd3("112")
                .introduction("안녕하세요")
                .fitnessLevel(FitnessLevel.BEGINNER)
                .companionStyle(CompanionStyle.GROUP)
                .fitnessEagerness(FitnessEagerness.EAGER)
                .fitnessObjective(FitnessObjective.BULK_UP)
                .fitnessKind(FitnessKind.FUNCTIONAL)
                .build());

        when(this.memberService.findMember(10060L)).thenThrow(new MemberNotFoundException(10060L));
        when(this.memberService.updateMember(eq(10500L), any(MemberUpdateRequestDto.class)))
                .thenReturn(MemberUpdateResponseDto.builder().build());
        when(this.memberService.updateMember(eq(10600L), any()))
                .thenThrow(new MemberNotFoundException(10600L));
    }

    @DisplayName("POST /hf/members - success")
    @Test
    void memberCreation_success() throws Exception {
        this.mockMvc.perform(multipart("/hf/members")
                        .param("id", "new@gmail.com")
                        .param("nickname", "새로운인간")
                        .param("name", "김샘플")
                        .param("birthDate", "1997-09-16")
                        .param("gender", "MALE")
                        .param("phoneNumber", "010-1234-5555")
                        .param("cd1", "01")
                        .param("cd2", "110")
                        .param("cd3", "112")
                        .param("introduction", "안녕하세요")
                        .param("fitnessLevel", "BEGINNER")
                        .param("companionStyle", "GROUP")
                        .param("fitnessEagerness", "EAGER")
                        .param("fitnessObjective", "BULK_UP")
                        .param("fitnessKind", "FUNCTIONAL"))
                .andDo(log())
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {
                            "statusCode": 201,
                            "statusCodeSeries": 2,
                            "content": {
                                "loginId": "new@gmail.com",
                                "email": "new@gmail.com",
                                "role": "ROLE_MEMBER",
                                "nickname": "새로운인간",
                                "gender": "MALE"
                            }
                        }
                        """));

    }

    @DisplayName("POST /hf/members - failure")
    @Test
    void memberCreation_failure() throws Exception {
        this.mockMvc.perform(multipart("/hf/members")
                        .param("id", "duplicate@gmail.com")
                        .param("nickname", "샘플닉네임")
                        .param("name", "김샘플")
                        .param("birthDate", "1997-09-16")
                        .param("gender", "MALE")
                        .param("phoneNumber", "010-1234-5555")
                        .param("cd1", "01")
                        .param("cd2", "110")
                        .param("cd3", "112")
                        .param("introduction", "안녕하세요")
                        .param("fitnessLevel", "BEGINNER")
                        .param("companionStyle", "GROUP")
                        .param("fitnessEagerness", "EAGER")
                        .param("fitnessObjective", "BULK_UP")
                        .param("fitnessKind", "FUNCTIONAL"))
                .andDo(log())
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "statusCode": 400,
                            "errorCode": 201,
                            "errorName": "MEMBER_ALREADY_EXISTS"
                        }
                        """));
    }

    @DisplayName("GET /hf/members/{memberId} - succeess to find member")
    @Test
    void findMember_success() throws Exception {
        String responseBodyAsString = this.mockMvc.perform(get("/hf/members/{memberId}", 10050)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "statusCode": 200,
                            "statusCodeSeries": 2,
                            "content": {
                                "loginId": "sample@gmail.com",
                                "nickname": "샘플닉네임",
                                "role": "ROLE_MEMBER",
                                "email": "sample@gmail.com",
                                "gender": "MALE",
                                "introduction": "안녕하세요",
                                "fitnessLevel": "BEGINNER"
                            }
                        }
                        """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        log.info("response={}", responseBodyAsString);

        // TODO: 이 코드를 TestSpecController로 이동
//        ApiBasicResponse<MemberDto> responseBody = this.objectMapper.readValue(responseBodyAsString, new TypeReference<>() {
//        });
//
//        List<SpecDto> responseSpecs = responseBody.getContent().getSpecs();
//        List<SpecDto> expected = getSampleSpecs();
//        assertThat(responseSpecs).size().isEqualTo(expected.size());
//        assertThat(responseSpecs.stream().map(SpecDto::getStartDate).toArray(LocalDate[]::new))
//                .containsExactlyInAnyOrder(expected.stream().map(SpecDto::getStartDate).toArray(LocalDate[]::new));
//        assertThat(responseSpecs.stream().map(SpecDto::getEndDate).toArray(LocalDate[]::new))
//                .containsExactlyInAnyOrder(expected.stream().map(SpecDto::getEndDate).toArray(LocalDate[]::new));
//        assertThat(responseSpecs.stream().map(SpecDto::getTitle).toArray(String[]::new))
//                .containsExactlyInAnyOrder(expected.stream().map(SpecDto::getTitle).toArray(String[]::new));
//        assertThat(responseSpecs.stream().map(SpecDto::getDescription).toArray(String[]::new))
//                .containsExactlyInAnyOrder(expected.stream().map(SpecDto::getDescription).toArray(String[]::new));
//        assertThat(responseSpecs.stream().map(SpecDto::isCurrent).toArray(Boolean[]::new))
//                .containsExactlyInAnyOrder(expected.stream().map(SpecDto::isCurrent).toArray(Boolean[]::new));
    }

    @DisplayName("GET /hf/members/{memberId} - Member not found")
    @Test
    void findMember_memberNotFound() throws Exception {
        this.mockMvc.perform(get("/hf/members/{memberId}", 10060)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {
                            "statusCode": 404,
                            "statusCodeSeries": 4,
                            "errorCode": 200,
                            "errorName": "MEMBER_OF_THE_MEMBER_ID_NOT_FOUND"
                        }
                        """));
    }

    @DisplayName("PATCH /hf/members/{memberId} - success")
    @Test
    void updateMember_success() throws Exception {
        this.mockMvc.perform(patch("/hf/members/{memberId}", 10500)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(log())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "statusCode": 200,
                            "statusCodeSeries": 2
                        }
                        """));
    }

    @DisplayName("PATCH /hf/members/{memberId} - Member not found")
    @Test
    void updateMember_memberNotFound() throws Exception {
        this.mockMvc.perform(patch("/hf/members/{memberId}", 10600)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(log())
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {
                            "statusCode": 404,
                            "statusCodeSeries": 4,
                            "errorCode": 200,
                            "errorName": "MEMBER_OF_THE_MEMBER_ID_NOT_FOUND"
                        }
                        """));
    }
}