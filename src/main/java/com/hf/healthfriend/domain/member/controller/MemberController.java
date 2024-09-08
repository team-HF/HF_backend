package com.hf.healthfriend.domain.member.controller;

import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.domain.member.service.MemberService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import com.hf.healthfriend.global.spec.ApiErrorResponse;
import com.hf.healthfriend.global.spec.schema.MemberCreationResponseSchema;
import com.hf.healthfriend.global.spec.schema.MemberResponseSchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "회원 생성"
    )
    @ApiResponses({
            @ApiResponse(
                    description = "회원 생성 성공",
                    responseCode = "201",
                    headers = @Header(name = "Location", description = "생성된 회원의 리소스 경로"),
                    content = @Content(schema = @Schema(implementation = MemberCreationResponseSchema.class))
            ),
            @ApiResponse(
                    description = "회원 중복 등록 에러",
                    responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiBasicResponse<MemberCreationResponseDto>> createMember(@RequestBody MemberCreationRequestDto requestBody) throws URISyntaxException {
        log.info("Request Body:\n{}", requestBody);
        MemberCreationResponseDto result = this.memberService.createMember(requestBody);
        return ResponseEntity.created(new URI("/members/" + result.getMemberId()))
                .body(ApiBasicResponse.of(result, HttpStatus.CREATED));
    }

    @Operation(summary = "회원 찾기")
    @ApiResponses({
            @ApiResponse(
                    description = "회원 찾기 성공",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = MemberResponseSchema.class))
            ),
            @ApiResponse(
                    description = "없는 회원 검색",
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiBasicResponse<MemberDto>> findMember(@PathVariable String memberId) {
        log.info("Find member of id={}", memberId);
        return ResponseEntity.ok(ApiBasicResponse.of(this.memberService.findMember(memberId), HttpStatus.OK));
    }
}
