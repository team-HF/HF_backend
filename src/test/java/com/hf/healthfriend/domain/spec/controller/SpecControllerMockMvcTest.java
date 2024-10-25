package com.hf.healthfriend.domain.spec.controller;

import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.spec.constants.SpecUpdateType;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import com.hf.healthfriend.domain.spec.dto.request.SpecUpdateRequestDto;
import com.hf.healthfriend.domain.spec.dto.response.SpecUpdateResponseDto;
import com.hf.healthfriend.domain.spec.service.SpecService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockBean(JpaMetamodelMappingContext.class)
@MockBean(MemberRepository.class)
class SpecControllerMockMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SpecService specService;

    static class IsSpecDtoSame implements ArgumentMatcher<List<SpecDto>> {
        List<SpecDto> specDtos;

        IsSpecDtoSame(List<SpecDto> specDtos) {
            this.specDtos = specDtos;
        }

        @Override
        public boolean matches(List<SpecDto> argument) {
            return this.specDtos.stream()
                    .allMatch((specDto) ->
                            argument.stream()
                                    .anyMatch((arg) ->
                                            isTwoObjectSame(arg.getSpecId(), specDto.getSpecId())
                                                    && isTwoObjectSame(arg.getTitle(), specDto.getTitle())
                                                    && isTwoObjectSame(arg.getDescription(), specDto.getDescription())
                                                    && isTwoObjectSame(arg.getStartDate(), specDto.getStartDate())
                                                    && isTwoObjectSame(arg.isCurrent(), specDto.isCurrent())
                                                    && isTwoObjectSame(arg.getEndDate(), specDto.getEndDate())));
        }
    }

    static class IsSpecUpdateRequestDtoSame implements ArgumentMatcher<List<SpecUpdateRequestDto>> {
        List<SpecUpdateRequestDto> specUpdateRequestDtos;

        IsSpecUpdateRequestDtoSame(List<SpecUpdateRequestDto> specUpdateRequestDtos) {
            this.specUpdateRequestDtos = specUpdateRequestDtos;
        }

        @Override
        public boolean matches(List<SpecUpdateRequestDto> argument) {
            return this.specUpdateRequestDtos.stream()
                    .allMatch((specDto) ->
                            argument.stream()
                                    .anyMatch((arg) ->
                                            isTwoObjectSame(arg.getSpecId(), specDto.getSpecId())
                                                    && isTwoObjectSame(arg.getSpecUpdateType(), specDto.getSpecUpdateType())
                                                    && (
                                                    arg.getSpec() == null && specDto.getSpec() == null
                                                            || (arg.getSpec() != null && specDto.getSpec() != null
                                                            && isTwoObjectSame(arg.getSpec().getTitle(), specDto.getSpec().getTitle())
                                                            && isTwoObjectSame(arg.getSpec().getDescription(), specDto.getSpec().getDescription())
                                                            && isTwoObjectSame(arg.getSpec().getStartDate(), specDto.getSpec().getStartDate())
                                                            && isTwoObjectSame(arg.getSpec().isCurrent(), specDto.getSpec().isCurrent())
                                                            && isTwoObjectSame(arg.getSpec().getEndDate(), specDto.getSpec().getEndDate()))

                                            )
                                    ));
        }
    }

    private static boolean isTwoObjectSame(Object obj1, Object obj2) {
        if (obj1 == null || obj2 == null) {
            return obj1 == null && obj2 == null;
        }
        return obj1.equals(obj2);
    }

    @DisplayName("POST - /hf/members/{memberId}/specs - 경력 및 수상 이력 추가 성공")
    @Test
    void addSpecs() throws Exception {
        // Given
        List<SpecDto> given = List.of(
                SpecDto.builder()
                        .title("경력1")
                        .startDate(LocalDate.of(2017, 11, 23))
                        .endDate(LocalDate.of(2018, 11, 12))
                        .isCurrent(false)
                        .description("종료된 경력")
                        .build(),
                SpecDto.builder()
                        .title("경력2")
                        .startDate(LocalDate.of(2024, 8, 24))
                        .isCurrent(true)
                        .description("현재 진행 중인 경력")
                        .build(),
                SpecDto.builder()
                        .title("수상1")
                        .startDate(LocalDate.of(2020, 11, 23))
                        .isCurrent(false)
                        .description("")
                        .build()
        );
        when(this.specService.addSpec(eq(1000L), argThat(new IsSpecDtoSame(given)))).thenReturn(List.of(100L, 101L, 102L));

        // When
        ResultActions result = this.mockMvc.perform(
                post("/hf/members/{memberId}/specs", 1000L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                    {
                                        "startDate": "2017-11-23",
                                        "endDate": "2018-11-12",
                                        "isCurrent": false,
                                        "title": "경력1",
                                        "description": "종료된 경력"
                                    },
                                    {
                                        "startDate": "2024-08-24",
                                        "endDate": null,
                                        "isCurrent": true,
                                        "title": "경력2",
                                        "description": "현재 진행 중인 경력"
                                    },
                                    {
                                        "startDate": "2020-11-23",
                                        "endDate": null,
                                        "isCurrent": false,
                                        "title": "수상1",
                                        "description": ""
                                    }
                                ]
                                """)
        ).andDo(log()).andDo(print());

        // Then
        result.andExpect(status().isCreated())
                .andExpect(content()
                        .json("""
                                {
                                    "statusCode": 201,
                                    "statusCodeSeries": 2,
                                    "content": [
                                        100,
                                        101,
                                        102
                                    ]
                                }
                                """));
    }

    @DisplayName("POST - /hf/members/{memberId}/specs - 경력 및 수상 이력 추가 실패 - 404 - 없는 회원에 대한 정보 조회")
    @Test
    void addSpecs_fail() throws Exception {
        // Given
        List<SpecDto> given = List.of(
                SpecDto.builder()
                        .title("경력1")
                        .startDate(LocalDate.of(2017, 11, 23))
                        .endDate(LocalDate.of(2018, 11, 12))
                        .isCurrent(false)
                        .description("종료된 경력")
                        .build(),
                SpecDto.builder()
                        .title("경력2")
                        .startDate(LocalDate.of(2024, 8, 24))
                        .isCurrent(true)
                        .description("현재 진행 중인 경력")
                        .build(),
                SpecDto.builder()
                        .title("수상1")
                        .startDate(LocalDate.of(2020, 11, 23))
                        .isCurrent(false)
                        .description("")
                        .build()
        );
        when(this.specService.addSpec(eq(1001L), argThat(new IsSpecDtoSame(given)))).thenThrow(new MemberNotFoundException(1001L));

        // When
        ResultActions result = this.mockMvc.perform(
                post("/hf/members/{memberId}/specs", 1001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                    {
                                        "startDate": "2017-11-23",
                                        "endDate": "2018-11-12",
                                        "isCurrent": false,
                                        "title": "경력1",
                                        "description": "종료된 경력"
                                    },
                                    {
                                        "startDate": "2024-08-24",
                                        "endDate": null,
                                        "isCurrent": true,
                                        "title": "경력2",
                                        "description": "현재 진행 중인 경력"
                                    },
                                    {
                                        "startDate": "2020-11-23",
                                        "endDate": null,
                                        "isCurrent": false,
                                        "title": "수상1",
                                        "description": ""
                                    }
                                ]
                                """)
        ).andDo(log()).andDo(print());

        // Then
        result.andExpect(status().isNotFound())
                .andExpect(content()
                        .json("""
                                {
                                    "errorCode": "SP001",
                                    "statusCode": 40400,
                                    "errorName": "MEMBER_NOT_FOUND",
                                    "statusCodeSeries": 4
                                }
                                """));
    }

    @DisplayName("GET /hf/members/{memberId}/specs - 특정 회원이 가진 경력 및 수상 이력 조회 성공")
    @Test
    void getSpecsOfMember_success() throws Exception {
        // Given
        when(this.specService.getSpecsOfMember(1000L)).thenReturn(List.of(
                SpecDto.builder()
                        .specId(1000L)
                        .title("스펙1")
                        .startDate(LocalDate.of(2018, 3, 5))
                        .endDate(LocalDate.of(2019, 5, 7))
                        .isCurrent(false)
                        .description("")
                        .build()
        ));

        // When
        ResultActions result = this.mockMvc.perform(get("/hf/members/{memberId}/specs", 1000L))
                .andDo(print()).andDo(log());

        // Then
        result.andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "statusCode": 200,
                            "statusCodeSeries": 2,
                            "content": [
                                {
                                    "specId": 1000,
                                    "title": "스펙1",
                                    "startDate": "2018-03-05",
                                    "endDate": "2019-05-07",
                                    "isCurrent": false,
                                    "description": ""
                                }
                            ]
                        }
                        """));
    }

    @DisplayName("PUT /hf/members/{memberId}/specs - 경력 및 수상 이력 수정 성공")
    @Test
    void updateSpecs_success() throws Exception {
        // Given
        List<SpecUpdateRequestDto> given = List.of(
                SpecUpdateRequestDto.builder()
                        .specUpdateType(SpecUpdateType.INSERT)
                        .spec(
                                SpecDto.builder()
                                        .startDate(LocalDate.of(2020, 10, 21))
                                        .endDate(LocalDate.of(2022, 10, 21))
                                        .title("새 스펙")
                                        .description("스펙")
                                        .isCurrent(false)
                                        .build()
                        )
                        .build(),
                SpecUpdateRequestDto.builder()
                        .specUpdateType(SpecUpdateType.UPDATE)
                        .specId(100L)
                        .spec(
                                SpecDto.builder()
                                        .startDate(LocalDate.of(2020, 10, 21))
                                        .endDate(LocalDate.of(2022, 10, 21))
                                        .title("스포애니 전문 트레이너")
                                        .description("원래는 에이블짐 트레이너였음")
                                        .isCurrent(false)
                                        .build()
                        )
                        .build(),
                SpecUpdateRequestDto.builder()
                        .specUpdateType(SpecUpdateType.DELETE)
                        .specId(101L)
                        .build()
        );

        when(this.specService.updateSpecsOfMember(eq(10005L), argThat(new IsSpecUpdateRequestDtoSame(given))))
                .thenReturn(new SpecUpdateResponseDto(List.of(99L), List.of(100L), List.of(101L)));

        // When
        ResultActions result = this.mockMvc.perform(
                put("/hf/members/{memberId}/specs", 10005)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                    {
                                        "specUpdateType": "INSERT",
                                        "spec": {
                                            "startDate": "2020-10-21",
                                            "endDate": "2022-10-21",
                                            "title": "새 스펙",
                                            "description": "스펙",
                                            "isCurrent": false
                                        }
                                    },
                                    {
                                        "specUpdateType": "UPDATE",
                                        "specId": 100,
                                        "spec": {
                                            "startDate": "2020-10-21",
                                            "endDate": "2022-10-21",
                                            "title": "스포애니 전문 트레이너",
                                            "description": "원래는 에이블짐 트레이너였음",
                                            "isCurrent": false
                                        }
                                    },
                                    {
                                        "specUpdateType": "DELETE",
                                        "specId": 101
                                    }
                                ]
                                """)
        ).andDo(print()).andDo(log());

        // Then
        result.andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "statusCode": 200,
                            "statusCodeSeries": 2,
                            "content": {
                                "insertedSpecIds": [
                                    99
                                ],
                                "updatedSpecIds": [
                                    100
                                ],
                                "deletedSpecIds": [
                                    101
                                ]
                            }
                        }
                        """));
    }
}