package com.hf.healthfriend.domain.wish.controller;


import com.hf.healthfriend.domain.wish.dto.response.WishResponse;
import com.hf.healthfriend.domain.wish.entity.Wish;
import com.hf.healthfriend.domain.wish.service.WishService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/hf")
@RestController
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    @Operation(summary = "찜하기", responses = {
            @ApiResponse(responseCode = "200", description = "찜하기 성공"),
            @ApiResponse(responseCode = "400", description = "찜하기 실패")
    })
    @PostMapping("/wish")
    public ResponseEntity<ApiBasicResponse<Long>> create(long wisherId, long wishedId) {
        Long wishId = wishService.save(wisherId,wishedId);
        return ResponseEntity.ok(ApiBasicResponse.of(wishId, HttpStatus.OK));
    }

    @Operation(summary = "찜 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "찜 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "찜 삭제 실패")
    })
    @DeleteMapping("/wish/{wishId}")
    public ResponseEntity<ApiBasicResponse<Void>> delete(@PathVariable Long wishId) {
        wishService.delete(wishId);
        return ResponseEntity.ok(ApiBasicResponse.of(HttpStatus.OK));
    }

    @Operation(summary = "내가 찜한 목록 조회", responses = {
            @ApiResponse(responseCode = "200", description = "내가 찜한 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "내가 찜한 목록 조회 실패")
    })
    @GetMapping("/wish/wishedList")
    public ResponseEntity<ApiBasicResponse<List<WishResponse>>> getWishedList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam int size,
            long memberId) {
        return ResponseEntity.ok(ApiBasicResponse.of(wishService.getWishedList(page,size,memberId),HttpStatus.OK));
    }

    @Operation(summary = "특정인 찜한 목록 조회", responses = {
            @ApiResponse(responseCode = "200", description = "특정인 찜한 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "특정인 찜한 목록 조회 실패")
    })
    @GetMapping("/wish/wisherList")
    public ResponseEntity<ApiBasicResponse<List<WishResponse>>> getWisherList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam int size,
            long memberId) {
        return ResponseEntity.ok(ApiBasicResponse.of(wishService.getWisherList(page,size,memberId),HttpStatus.OK));
    }
}
