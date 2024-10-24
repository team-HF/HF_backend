package com.hf.healthfriend.domain.search.controller;

import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import com.hf.healthfriend.domain.post.constant.PostCategory;
import com.hf.healthfriend.domain.post.dto.response.PostListObject;
import com.hf.healthfriend.domain.search.constant.SearchCategory;
import com.hf.healthfriend.domain.search.dto.SearchResponse;
import com.hf.healthfriend.domain.search.service.SearchService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hf")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "통합 검색 목록 조회", responses = {
            @ApiResponse(responseCode = "200", description = "통합 검색 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "통합 검색 목록 조회 실패")
    })
    @GetMapping("/search")
    public ResponseEntity<ApiBasicResponse<SearchResponse>> getSearchList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam int size,
            @RequestParam @Nullable SearchCategory searchCategory,
            @RequestParam String keyword) {
        return ResponseEntity.ok(ApiBasicResponse.of(searchService.search(page,size,searchCategory,keyword),
                HttpStatus.OK));
    }
}
