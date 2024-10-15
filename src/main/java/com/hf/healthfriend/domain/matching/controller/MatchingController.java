package com.hf.healthfriend.domain.matching.controller;

import com.hf.healthfriend.domain.matching.dto.request.MatchingGetRequest;
import com.hf.healthfriend.domain.matching.dto.response.MatchingGetResponse;
import com.hf.healthfriend.domain.matching.service.MatchingService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@RequestMapping("/hf")
@Slf4j
public class MatchingController {
    private final MatchingService matchingService;

    @Operation(summary = "매칭 추천멤버 목록 조회", responses = {
            @ApiResponse(responseCode = "200", description = "매칭 추천멤버 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "매칭 추천멤버 목록 조회 실패")
    })
    @GetMapping("/matching")
    public ResponseEntity<ApiBasicResponse<List<MatchingGetResponse>>> getMatchingRecommendMemberList(MatchingGetRequest matchingGetRequest, int pageNumber) {
        return ResponseEntity.ok(ApiBasicResponse.of(matchingService.getMatchingRecommendMemberList(matchingGetRequest,pageNumber), HttpStatus.OK));
    }
}
