package com.hf.healthfriend.domain.member.controller;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exceptionhandler.MemberExceptionHandlerControllerAdvice;
import com.hf.healthfriend.domain.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.Month;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@ActiveProfiles({"constants", "secret"})
@Transactional
class TestMemberController {

    MockMvc mockMvc;

    Member sampleMember;

    @Autowired
    WebApplicationContext context;

    @Autowired
    MemberExceptionHandlerControllerAdvice memberExceptionHandlerControllerAdvice;

    @Autowired
    MemberService memberService;

    @BeforeEach
    void beforeEach() {
        MemberCreationRequestDto creationRequestDtoDuplicate = MemberCreationRequestDto.builder()
                .id("sample@gmail.com")
                .nickname("샘플닉네임")
                .birthDate(LocalDate.of(1997, Month.SEPTEMBER, 16))
                .gender(Gender.MALE)
                .introduction("안녕하세요")
                .fitnessLevel(FitnessLevel.BEGINNER)
                .companionStyle(CompanionStyle.GROUP)
                .fitnessEagerness(FitnessEagerness.EAGER)
                .fitnessObjective(FitnessObjective.BULK_UP)
                .fitnessKind(FitnessKind.FUNCTIONAL)
                .build();

        this.memberService.createMember(creationRequestDtoDuplicate);

        this.mockMvc = MockMvcBuilders.standaloneSetup(new MemberController(this.memberService))
                .setControllerAdvice(this.memberExceptionHandlerControllerAdvice)
                .build();
    }

    @DisplayName("POST /members - success")
    @Test
    void memberCreation_success() throws Exception {
        this.mockMvc.perform(post("/members")
                        .content("""
                                {
                                    "id": "new@gmail.com",
                                    "nickname": "새로운인간",
                                    "birthDate": "1997-09-16",
                                    "gender": "MALE",
                                    "introduction": "안녕하세요",
                                    "fitnessLevel": "BEGINNER",
                                    "companionStyle": "GROUP",
                                    "fitnessEagerness": "EAGER",
                                    "fitnessObjective": "BULK_UP",
                                    "fitnessKind": "FUNCTIONAL"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(log())
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {
                            "statusCode": 201,
                            "statusCodeSeries": 2,
                            "content": {
                                "memberId": "new@gmail.com",
                                "email": "new@gmail.com",
                                "role": "ROLE_MEMBER",
                                "nickname": "새로운인간",
                                "gender": "MALE"
                            }
                        }
                        """));

    }

    @DisplayName("POST /members - failure")
    @Test
    void memberCreation_failure() throws Exception {
        this.mockMvc.perform(multipart("/members")
                        .param("id", "sample@gmail.com")
                        .param("nickname", "샘플닉네임")
                        .param("birthDate", "1997-09-16")
                        .param("gender", "MALE")
                        .param("introduction", "안녕하세요")
                        .param("fitnessLevel", "BEGINNER")
                        .param("companionStyle", "GROUP")
                        .param("fitnessEagerness", "EAGER")
                        .param("fitnessObjective", "BULK_UP")
                        .param("fitnessKind", "FUNCTIONAL")
                        .with(csrf()))
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

    @DisplayName("GET /members/{memberId} - succeess to find member")
    @Test
    void findMember_success() throws Exception {
        this.mockMvc.perform(get("/members/{memberId}", "sample@gmail.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "statusCode": 200,
                            "statusCodeSeries": 2,
                            "content": {
                                "memberId": "sample@gmail.com",
                                "nickname": "샘플닉네임",
                                "role": "ROLE_MEMBER",
                                "email": "sample@gmail.com",
                                "gender": "MALE",
                                "introduction": "안녕하세요",
                                "fitnessLevel": "BEGINNER"
                            }
                        }
                        """));
    }

    @DisplayName("GET /members/{memberId} - Member not found")
    @Test
    void findMember_memberNotFound() throws Exception {
        this.mockMvc.perform(get("/members/{memberId}", "no@such.member")
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

    @DisplayName("PATCH /members/{memberId} - success")
    @Test
    void updateMember_success() throws Exception {
        this.mockMvc.perform(patch("/members/{memberId}", "sample@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
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

    @DisplayName("PATCH /members/{memberId} - Member not found")
    @Test
    void updateMember_memberNotFound() throws Exception {
        this.mockMvc.perform(patch("/members/{memberId}", "no@such.member")
                        .contentType(MediaType.APPLICATION_JSON)
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