package com.hf.healthfriend.domain.member.controller;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.request.MemberUpdateRequestDto;
import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.domain.member.dto.response.MemberUpdateResponseDto;
import com.hf.healthfriend.domain.member.dto.response.ProfileResponseDto;
import com.hf.healthfriend.domain.member.exception.DuplicateMemberCreationException;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.member.service.MemberService;
import com.hf.healthfriend.domain.review.constants.EvaluationType;
import com.hf.healthfriend.domain.review.dto.response.ReviewDetailPerEvaluationType;
import com.hf.healthfriend.domain.review.dto.response.SimpleReviewResponseDto;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import com.hf.healthfriend.global.config.BeanConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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
@Import(BeanConfig.class)
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
        doReturn(MemberCreationResponseDto.builder()
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
                .specIds(List.of(1000L))
                .build())

                .when(this.memberService)
                .createMember(argThat(new MemberCreationRequestDtoArgumentMatcher(given)));

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

        doThrow(new DuplicateMemberCreationException(duplicateCreation.getId()))
                .when(this.memberService)
                .createMember(argThat(new MemberCreationRequestDtoArgumentMatcher(duplicateCreation)));

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

    @AllArgsConstructor
    static class MemberCreationRequestDtoArgumentMatcher implements ArgumentMatcher<MemberCreationRequestDto> {
        MemberCreationRequestDto requestDto;


        @Override
        public boolean matches(MemberCreationRequestDto argument) {
            return this.requestDto.getId().equals(argument.getId());
        }
    }

    @DisplayName("POST /hf/members - success")
    @Test
    void memberCreation_success() throws Exception {
        this.mockMvc.perform(post("/hf/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "new@gmail.com",
                                    "name": "김샘플",
                                    "nickname": "새로운인간",
                                    "profileImageFileExtension": "jpg",
                                    "birthDate": "1997-09-16",
                                    "gender": "MALE",
                                    "cd1": "01",
                                    "cd2": "110",
                                    "cd3": "112",
                                    "introduction": "안녕하세요",
                                    "fitnessLevel": "BEGINNER",
                                    "companionStyle": "GROUP",
                                    "fitnessEagerness": "EAGER",
                                    "fitnessObjective": "BULK_UP",
                                    "fitnessKind": "FUNCTIONAL",
                                    "specs": [
                                        {
                                            "startDate": "2021-10",
                                            "endDate": "2023-12",
                                            "title": "Hello",
                                            "description": "Good bye",
                                            "isCurrent": false
                                        }
                                    ]
                                }
                                """))
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
                                "gender": "MALE",
                                "specIds": [ 1000 ]
                            }
                        }
                        """));

    }

    @DisplayName("POST /hf/members - failure")
    @Test
    void memberCreation_failure() throws Exception {
        this.mockMvc.perform(post("/hf/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {
                                    "id": "duplicate@gmail.com",
                                    "name": "김샘플",
                                    "nickname": "새로운인간",
                                    "profileImageFileExtension": null,
                                    "birthDate": "1997-09-16",
                                    "gender": "MALE",
                                    "cd1": "01",
                                    "cd2": "110",
                                    "cd3": "112",
                                    "introduction": "안녕하세요",
                                    "fitnessLevel": "BEGINNER",
                                    "companionStyle": "GROUP",
                                    "fitnessEagerness": "EAGER",
                                    "fitnessObjective": "BULK_UP",
                                    "fitnessKind": "FUNCTIONAL",
                                    "specs": [
                                        {
                                            "startDate": "2021-10",
                                            "endDate": "2023-12",
                                            "title": "Hello",
                                            "description": "Good bye",
                                            "isCurrent": false
                                        }
                                    ]
                                }
                                """))
                .andDo(log())
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "statusCode": 40003,
                            "errorCode": "MB004",
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
                            "statusCode": 40401,
                            "statusCodeSeries": 4,
                            "errorCode": "MB001",
                            "errorName": "MEMBER_NOT_FOUND"
                        }
                        """));
    }

    @DisplayName("PATCH /hf/members/{memberId} - success")
    @Test
    void updateMember_success() throws Exception {
        this.mockMvc.perform(patch("/hf/members/{memberId}", 10500)
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

    @DisplayName("PATCH /hf/members/{memberId} - Member not found")
    @Test
    void updateMember_memberNotFound() throws Exception {
        this.mockMvc.perform(patch("/hf/members/{memberId}", 10600)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(log())
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {
                            "statusCode": 40401,
                            "statusCodeSeries": 4,
                            "errorCode": "MB001",
                            "errorName": "MEMBER_NOT_FOUND"
                        }
                        """));
    }

    @DisplayName("GET /hf/members/{memberId}/profile - success")
    @Test
    void getProfile_success() throws Exception {
        // Given
        final Long memberId = 20000L;
        when(this.memberService.getProfileOfMember(memberId)).thenReturn(
                ProfileResponseDto.builder()
                        .memberId(memberId)
                        .introduction("안녕하세요!")
                        .specs(
                                List.of(
                                        SpecDto.builder()
                                                .specId(1002L)
                                                .startDate(LocalDate.of(2022, 3, 1))
                                                .endDate(null)
                                                .isCurrent(true)
                                                .title("스포애니 전문 트레이너")
                                                .description("스포애니에서 현재까지 근무 중")
                                                .build(),
                                        SpecDto.builder()
                                                .specId(1001L)
                                                .startDate(LocalDate.of(2021, 4, 1))
                                                .endDate(null)
                                                .isCurrent(false)
                                                .title("보디빌딩 대회 국방부장관상")
                                                .description("수상 이력입니다. (endDate null, isCurrent false일 경우 수상 이력)")
                                                .build(),
                                        SpecDto.builder()
                                                .specId(1000L)
                                                .startDate(LocalDate.of(2019, 3, 1))
                                                .endDate(LocalDate.of(2020, 12, 1))
                                                .isCurrent(false)
                                                .title("15 전투비행단 체력단련실 지박령")
                                                .description("")
                                                .build()
                                )
                        )
                        .reviews(
                                List.of(
                                        new SimpleReviewResponseDto(
                                                EvaluationType.GOOD,
                                                List.of(
                                                        new ReviewDetailPerEvaluationType(
                                                                1, 12L
                                                        ),
                                                        new ReviewDetailPerEvaluationType(
                                                                2, 9L
                                                        )
                                                )
                                        ),
                                        new SimpleReviewResponseDto(
                                                EvaluationType.NOT_GOOD,
                                                List.of(
                                                        new ReviewDetailPerEvaluationType(
                                                                3, 8L
                                                        ),
                                                        new ReviewDetailPerEvaluationType(
                                                                1, 5L
                                                        )
                                                )
                                        )
                                )
                        )
                        .averageReviewScore(3.5)
                        .build()
        );

        this.mockMvc.perform(get("/hf/members/{memberId}/profile", memberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andDo(log())
                // TODO: PR #97과 병합되면 Spec의 startDate, endDate에서 day 부분은 제거
                .andExpect(content().json("""
                        {
                          "statusCode": 200,
                          "statusCodeSeries": 2,
                          "message": "회원 프로필 받아오기 성공",
                          "content": {
                            "memberId": 20000,
                            "introduction": "안녕하세요!",
                            "specs": [
                              {
                                "specId": 1002,
                                "startDate": "2022-03",
                                "endDate": null,
                                "isCurrent": true,
                                "title": "스포애니 전문 트레이너",
                                "description": "스포애니에서 현재까지 근무 중"
                              },
                              {
                                "specId": 1001,
                                "startDate": "2021-04",
                                "endDate": null,
                                "isCurrent": false,
                                "title": "보디빌딩 대회 국방부장관상",
                                "description": "수상 이력입니다. (endDate null, isCurrent false일 경우 수상 이력)"
                              },
                              {
                                "specId": 1000,
                                "startDate": "2019-03",
                                "endDate": "2020-12",
                                "isCurrent": false,
                                "title": "15 전투비행단 체력단련실 지박령",
                                "description": ""
                              }
                            ],
                            "reviews": [
                              {
                                "evaluationType": "GOOD",
                                "reviewDetailsPerEvaluationType": [
                                  {
                                    "reviewDetailId": 1,
                                    "reviewDetailCount": 12
                                  },
                                  {
                                    "reviewDetailId": 2,
                                    "reviewDetailCount": 9
                                  }
                                ]
                              },
                              {
                                "evaluationType": "NOT_GOOD",
                                "reviewDetailsPerEvaluationType": [
                                  {
                                    "reviewDetailId": 3,
                                    "reviewDetailCount": 8
                                  },
                                  {
                                    "reviewDetailId": 1,
                                    "reviewDetailCount": 5
                                  }
                                ]
                              }
                            ],
                            "averageReviewScore": 3.5
                          }
                        }
                        """));
    }
}