package com.hf.healthfriend.domain.member.controller;

import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.request.MemberUpdateRequestDto;
import com.hf.healthfriend.domain.member.dto.request.MembersRecommendRequest;
import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.domain.member.dto.response.MemberRecommendResponse;
import com.hf.healthfriend.domain.member.dto.response.MemberUpdateResponseDto;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/hf/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "회원 생성",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MemberCreationRequestDto.class),
                            examples = @ExampleObject("""
                                    {
                                        "id": "new@gmail.com",
                                        "name": "김샘플",
                                        "nickname": "새로운인간",
                                        "profileImagePresent": true,
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
                                    }
                                    """)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    description = "회원 생성 성공",
                    responseCode = "201",
                    headers = @Header(name = "Location", description = "생성된 회원의 리소스 경로"),
                    content = @Content(
                            schema = @Schema(implementation = MemberCreationResponseSchema.class),
                            examples = @ExampleObject("""
                                    {
                                        "statusCode": 201,
                                        "statusCodeSeries": 2,
                                        "message": null,
                                        "content": {
                                            "memberId": 1000,
                                            "loginId": "new@gmail.com",
                                            "email": "new@gmail.com",
                                            "role": "ROLE_MEMBER",
                                            "creationTime": "2024-10-29T15:08:06.022Z",
                                            "nickname": "새로운인간",
                                            "birthDate": "1997-09-16",
                                            "gender": "MALE",
                                            "introduction": "안녕하세요",
                                            "profileImageUploadUrl": "http://hf-aws-bucket.s3.aws.amazon.com?accessKey=acc#!$",
                                            "fitnessLevel": "ADVANCED",
                                            "companionStyle": "SMALL",
                                            "fitnessEagerness": "EAGER",
                                            "fitnessObjective": "BULK_UP",
                                            "fitnessKind": "HIGH_STRESS",
                                            "specIds": [
                                                10000,
                                                10001,
                                                10002
                                            ]
                                        }
                                    }
                                    """)
                    )
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
            @RequestBody @Valid MemberCreationRequestDto requestBody) throws URISyntaxException {
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

    @PatchMapping(value = "/{memberId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    @Operation(
            summary = "회원 업데이트",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = MemberUpdateRequestDto.class),
                            examples = @ExampleObject("""
                                    {
                                        "cd1": "10",
                                        "cd2": "111",
                                        "cd3": "123",
                                        "introduction": "수정된 소개",
                                        "fitnessLevel": "EAGER",
                                        "companionStyle": "GROUP",
                                        "fitnessEagerness": "EAGER",
                                        "fitnessObjective": "BULK_UP",
                                        "fitnessKind": "FUNCTIONAL",
                                        "profileImagePresent": true,
                                        "specUpdate": [
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
                                    }
                                    """)
                    )
            )
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
                                                                                  @RequestBody @Valid MemberUpdateRequestDto dto) {
        MemberUpdateResponseDto resultDto = this.memberService.updateMember(memberId, dto);
        return ResponseEntity.ok(ApiBasicResponse.of(resultDto, HttpStatus.OK));
    }

    @Operation(summary = "멤버 추천 목록 조회", responses = {
            @ApiResponse(responseCode = "200", description = "멤버 추천 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "멤버 추천 목록 조회 실패")
    })
    @GetMapping("/recommendMembers")
    public ResponseEntity<ApiBasicResponse<List<MemberRecommendResponse>>> getRecommendMembers(MembersRecommendRequest request, int page) {
        return ResponseEntity.ok(ApiBasicResponse.of(this.memberService.recommendMember(request,page), HttpStatus.OK));
    }
}
