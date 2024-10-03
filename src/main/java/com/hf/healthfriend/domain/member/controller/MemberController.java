package com.hf.healthfriend.domain.member.controller;

import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.request.MemberUpdateRequestDto;
import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.domain.member.dto.response.MemberUpdateResponseDto;
import com.hf.healthfriend.domain.member.service.MemberService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import com.hf.healthfriend.global.spec.ApiErrorResponse;
import com.hf.healthfriend.global.spec.schema.MemberCreationResponseSchema;
import com.hf.healthfriend.global.spec.schema.MemberResponseSchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/hf/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
                    description = "회원 중복 등록 에러 / Error Code: 201",
                    responseCode = "400",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject("""
                                    {
                                        "statusCode": 400,
                                        "statusCodeSeries": 4,
                                        "errorCode": 201,
                                        "errorName": "MEMBER_ALREADY_EXISTS",
                                        "message": "이미 존재하는 회원입니다"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    description = "현재 로그인한 회원이 자신과 다른 이름의 회원을 생성하려고 할 때 / Error Code: 101",
                    responseCode = "403",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject("""
                                    {
                                        "statusCode": 403,
                                        "statusCodeSeries": 4,
                                        "errorCode": 101,
                                        "errorName": "UNAUTHORIZED",
                                        "message": "허용되지 않은 접근입니다"
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<ApiBasicResponse<MemberCreationResponseDto>> createMember(
            @ModelAttribute @Valid MemberCreationRequestDto requestBody) throws URISyntaxException {
        log.info("Request Body:\n{}", requestBody);

        MemberCreationResponseDto result = this.memberService.createMember(requestBody);
        return ResponseEntity.created(new URI("/hr/members/" + result.getMemberId()))
                .body(ApiBasicResponse.of(result, HttpStatus.CREATED));
    }

    @GetMapping(value = "/{memberId}", produces = "application/json")
    @Operation(summary = "회원 찾기")
    @ApiResponses({
            @ApiResponse(
                    description = "회원 찾기 성공",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = MemberResponseSchema.class))
            ),
            @ApiResponse(
                    description = "없는 회원 검색 / Error Code: 200",
                    responseCode = "404",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject("""
                                    {
                                        "statusCode": 404,
                                        "statusCodeSeries": 4,
                                        "errorCode": 200,
                                        "errorName": "MEMBER_OF_THE_MEMBER_ID_NOT_FOUND",
                                        "message": "memberId에 해당하는 회원이 없습니다"
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<ApiBasicResponse<MemberDto>> findMember(@PathVariable("memberId") Long memberId) {
        log.info("Find member of id={}", memberId);
        return ResponseEntity.ok(ApiBasicResponse.of(this.memberService.findMember(memberId), HttpStatus.OK));
    }

    @PatchMapping(value = "/{memberId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    @Operation(
            summary = "회원 업데이트"
    )
    @ApiResponses({
            @ApiResponse(
                    description = "회원 업데이트 성공",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = MemberUpdateResponseDto.class))
            ),
            @ApiResponse(
                    description = "없는 회원 검색 / Error Code: 200",
                    responseCode = "404",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject("""
                                    {
                                        "statusCode": 404,
                                        "statusCodeSeries": 4,
                                        "errorCode": 200,
                                        "errorName": "MEMBER_OF_THE_MEMBER_ID_NOT_FOUND",
                                        "message": "memberId에 해당하는 회원이 없습니다"
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<ApiBasicResponse<MemberUpdateResponseDto>> updateMember(@PathVariable("memberId") Long memberId,
                                                                                  @ModelAttribute MemberUpdateRequestDto dto) {
        MemberUpdateResponseDto resultDto = this.memberService.updateMember(memberId, dto);
        return ResponseEntity.ok(ApiBasicResponse.of(resultDto, HttpStatus.OK));
    }
}
