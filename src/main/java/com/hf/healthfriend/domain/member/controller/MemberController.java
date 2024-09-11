package com.hf.healthfriend.domain.member.controller;

import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.domain.member.dto.MemberUpdateDto;
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
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping(consumes = "application/json", produces = "application/json")
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
    public ResponseEntity<ApiBasicResponse<MemberCreationResponseDto>> createMember(@RequestBody MemberCreationRequestDto requestBody) throws URISyntaxException {
        log.info("Request Body:\n{}", requestBody);

        // TODO: 여기 있으면 안 될 놈 같은데...
        // 근데 MemberAccessController에서 처리하려니 Request의 IOStream을 두 번 호출하면 예외가 발생한다.
        // 그렇다고 Service 레이어에서 처리하기엔 SecurityContextHolder와 같은 웹 기술이 비즈니스 로직에 노출되는 것이 마음에 들지 않는다.
        // AccessController에서 이 로직을 처리하는 방법을 찾아보자
        if (!isTheRequester(requestBody.getId())) {
            throw new AccessDeniedException("허용되지 않은 자원에 대한 접근입니다.");
        }

        MemberCreationResponseDto result = this.memberService.createMember(requestBody);
        return ResponseEntity.created(new URI("/members/" + result.getMemberId()))
                .body(ApiBasicResponse.of(result, HttpStatus.CREATED));
    }

    private boolean isTheRequester(String memberIdToBeCreated) {
        String authenticatedMemberId = SecurityContextHolder.getContext().getAuthentication().getName();
        return authenticatedMemberId.equals(memberIdToBeCreated);
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
    public ResponseEntity<ApiBasicResponse<MemberDto>> findMember(@PathVariable String memberId) {
        log.info("Find member of id={}", memberId);
        return ResponseEntity.ok(ApiBasicResponse.of(this.memberService.findMember(memberId), HttpStatus.OK));
    }

    @PatchMapping(value = "/{memberId}", consumes = "application/json", produces = "application/json")
    @Operation(summary = "회원 업데이트")
    @ApiResponses({
            @ApiResponse(
                    description = "회원 업데이트 성공",
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
    public ResponseEntity<ApiBasicResponse<MemberDto>> updateMember(@PathVariable String memberId,
                                                                    @RequestBody MemberUpdateDto dto) {
        MemberDto resultDto = this.memberService.updateMember(memberId, dto);
        return ResponseEntity.ok(ApiBasicResponse.of(resultDto, HttpStatus.OK));
    }
}
