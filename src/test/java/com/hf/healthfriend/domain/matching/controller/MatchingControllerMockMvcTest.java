package com.hf.healthfriend.domain.matching.controller;

import com.hf.healthfriend.domain.matching.constant.MatchingFetchType;
import com.hf.healthfriend.domain.matching.constant.MatchingStatus;
import com.hf.healthfriend.domain.matching.constant.MatchingStatusCondition;
import com.hf.healthfriend.domain.matching.dto.response.MatchingListResponseDto;
import com.hf.healthfriend.domain.matching.dto.response.PageResponseDto;
import com.hf.healthfriend.domain.matching.dto.response.ProfileOfMemberInMatchingResponseDto;
import com.hf.healthfriend.domain.matching.service.MatchingService;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatchingController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockBean(MemberRepository.class)
@MockBean(JpaMetamodelMappingContext.class)
class MatchingControllerMockMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MatchingService matchingService;

    List<MatchingListResponseDto> sampleResponses;

    @BeforeEach
    void initData() {
        List<MatchingListResponseDto> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int num = i + 1;
            ProfileOfMemberInMatchingResponseDto sampleProfile = ProfileOfMemberInMatchingResponseDto.builder()
                    .memberId(1000L + num)
                    .nickname("hello" + num)
                    .matchedCount(8L)
                    .build();
            MatchingListResponseDto sampleResponse = MatchingListResponseDto.builder()
                    .matchingId(1000L + num)
                    .meetingPlace("Somewhere")
                    .meetingPlaceAddress("Somewhere")
                    .matchingStatus(MatchingStatus.PENDING)
                    .meetingTime(LocalDateTime.now().plusDays(num))
                    .opponentInfo(sampleProfile)
                    .build();
            list.add(sampleResponse);
        }
        list.sort((r1, r2) -> {
            if (r1.meetingTime().isBefore(r2.meetingTime())) {
                return 1;
            } else if (r1.meetingTime().isAfter(r2.meetingTime())) {
                return -1;
            } else {
                return 0;
            }
        });
        this.sampleResponses = Collections.unmodifiableList(list);
    }

    @DisplayName("GET /hf/members/{memberId}/matchings - success")
    @CsvSource(value = {
            "1000,null,null,null,null",
            "1000,ALL,ALL,1,4",
            "1000,WHAT_I_RECEIVED,ALL,1,4",
            "1000,ALL,IN_PROGRESS,1,4",
            "1000,ALL,ALL,2,4",
            "1000,ALL,ALL,1,15"
    }, delimiter = ',', nullValues = "null")
    @ParameterizedTest
    void getMatchingList(Long memberId, MatchingFetchType fetchType, MatchingStatusCondition statusCondition, Integer page, Integer pageSize) throws Exception {
        // Given
        MatchingFetchType fetchTypeArg = fetchType == null ? MatchingFetchType.ALL : fetchType;
        MatchingStatusCondition statusConditionArg = statusCondition == null ? MatchingStatusCondition.ALL : statusCondition;
        Integer pageArg = page == null ? Integer.valueOf(1) : page;
        Integer pageSizeArg = pageSize == null ? Integer.valueOf(4) : pageSize;

        List<MatchingListResponseDto> filteredSample = this.sampleResponses.stream()
                .filter((matching) -> statusConditionArg == MatchingStatusCondition.ALL
                        || statusConditionArg.getCorrespondingStatus().contains(matching.matchingStatus()))
                .toList();
        PageRequest pageRequest = PageRequest.of(pageArg - 1, pageSizeArg);
        List<MatchingListResponseDto> subList = subListOfSampleResponse(filteredSample, pageRequest);

        Page<MatchingListResponseDto> queryResult = new PageImpl<>(
                subList, pageRequest, filteredSample.size()
        );

        when(this.matchingService.searchMatchingListOfMember(memberId, fetchTypeArg, statusConditionArg, pageArg, pageSizeArg))
                .thenReturn(PageResponseDto.<MatchingListResponseDto>builder()
                        .totalElementCount(queryResult.getTotalElements())
                        .totalPageCount(queryResult.getTotalPages())
                        .page(pageArg)
                        .pageSize(pageSizeArg)
                        .content(queryResult.getContent())
                        .build());

        // When
        String responseBodyAsString = this.mockMvc.perform(get("/hf/members/{memberId}/matchings", memberId)
                        .param("matchingFetchType", fetchType == null ? null : fetchType.name())
                        .param("matchingStatusCondition", statusCondition == null ? null : statusCondition.name())
                        .param("page", page == null ? null : page.toString())
                        .param("pageSize", pageSize == null ? null : pageSize.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(log()).andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JSONObject responseBody = new JSONObject(responseBodyAsString);

        // Then
        assertThat(responseBody.getInt("statusCode")).isEqualTo(200);

        // Page 검증
        assertThat(responseBody.getJSONObject("content").getInt("page")).isEqualTo(pageArg);
        assertThat(responseBody.getJSONObject("content").getInt("pageSize")).isEqualTo(pageSizeArg);
        assertThat(responseBody.getJSONObject("content").getInt("totalElementCount"))
                .isEqualTo(queryResult.getTotalElements());
        assertThat(responseBody.getJSONObject("content").getInt("totalPageCount"))
                .isEqualTo(queryResult.getTotalPages());

        // Content 검증
        JSONArray content = responseBody.getJSONObject("content").getJSONArray("content");

        assertThat(content).size().isEqualTo(subList.size());

        List<Long> matchingIds = new ArrayList<>();
        for (int i = 0; i < content.length(); i++) {
            matchingIds.add(content.getJSONObject(i).getLong("matchingId"));
        }
        assertThat(matchingIds)
                .containsExactly(queryResult.getContent().stream().map(MatchingListResponseDto::matchingId).toArray(Long[]::new));
    }

    private List<MatchingListResponseDto> subListOfSampleResponse(List<MatchingListResponseDto> original, Pageable page) {
        int offset = (int) page.getOffset();
        int pageSize = page.getPageSize();
        if (offset > original.size() - 1) {
            return List.of();
        }
        return original.subList(offset, Math.min(offset + pageSize, original.size()));
    }

    @DisplayName("GET /hf/members/{memberId}/matchings - 404 - 존재하지 않는 회원")
    @Test
    void getMatchingList_404_MEMBER_NOT_FOUND() throws Exception {
        doThrow(new MemberNotFoundException(1000L))
                .when(this.matchingService)
                .searchMatchingListOfMember(any(), any(), any(), anyInt(), anyInt());

        this.mockMvc.perform(get("/hf/members/{memberId}/matchings", 1000))
                .andDo(print()).andDo(log())
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
}