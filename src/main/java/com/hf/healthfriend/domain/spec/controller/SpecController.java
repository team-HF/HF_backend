package com.hf.healthfriend.domain.spec.controller;

import com.hf.healthfriend.domain.spec.controller.schema.ListSpecDtoSchema;
import com.hf.healthfriend.domain.spec.controller.schema.SpecUpdateRequestDtoListSchema;
import com.hf.healthfriend.domain.spec.controller.schema.SpecUpdateResponseDtoSchema;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import com.hf.healthfriend.domain.spec.dto.request.SpecUpdateRequestDto;
import com.hf.healthfriend.domain.spec.dto.response.SpecUpdateResponseDto;
import com.hf.healthfriend.domain.spec.service.SpecService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import com.hf.healthfriend.global.spec.schema.LongTypeListSchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/hf")
public class SpecController {
    private final SpecService specService;

    @PostMapping("/members/{memberId}/specs")
    @Operation(
            summary = "경력 및 수상 이력 추가",
            parameters = @Parameter(
                    name = "회원 ID",
                    required = true,
                    example = "20000"
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = ListSpecDtoSchema.class),
                            examples = @ExampleObject("""
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

                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "경력 및 수상 이력 등록 성공",
                            content = @Content(
                                    schema = @Schema(implementation = LongTypeListSchema.class),
                                    examples = @ExampleObject("""
                                            {
                                                "statusCode": 201,
                                                "statusCodeSeries": 2,
                                                "content": [
                                                    10000,
                                                    10001,
                                                    10002
                                                ]
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 회원에 대한 경력 추가를 시도할 경우",
                            content = @Content(
                                    schema = @Schema(implementation = BasicErrorResponse.class),
                                    examples = @ExampleObject("""
                                            {
                                                "errorCode": "SP001",
                                                "statusCode": 40400,
                                                "message": "해당 회원이 존재하지 않습니다",
                                                "errorName": "MEMBER_NOT_FOUND",
                                                "statusCodeSeries": 4
                                            }
                                            """)
                            )
                    )
            }
    )
    public ResponseEntity<ApiBasicResponse<List<Long>>> addSpecs(@PathVariable("memberId") Long memberId,
                                                                 @RequestBody @Valid List<SpecDto> dtoList) {
        List<Long> result = this.specService.addSpec(memberId, dtoList);
        return new ResponseEntity<>(
                ApiBasicResponse.of(result, HttpStatus.CREATED),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/members/{memberId}/specs")
    @Operation(
            summary = "특정 회원이 가진 경력 및 수상이력 조회",
            parameters = @Parameter(
                    name = "회원 ID",
                    required = true,
                    example = "10005"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = ListSpecDtoSchema.class),
                                    examples = @ExampleObject(
                                            description = "startDate를 기준으로 내림차순 정렬",
                                            value = """
                                                    {
                                                        "statusCode": 200,
                                                        "statusCodeSeries": 2,
                                                        content: [
                                                            {
                                                                "specId": 879,
                                                                "startDate": "2019-03-24",
                                                                "endDate": null,
                                                                "isCurrent": true,
                                                                "title": "진행 중인 경력",
                                                                "description": "현재 진행 중"
                                                            },
                                                            {
                                                                "specId": 1001,
                                                                "startDate": "2018-01-23",
                                                                "endDate": null,
                                                                "isCurrent": false,
                                                                "title": "수상1",
                                                                "description": "수상"
                                                            },
                                                            {
                                                                "specId": 1000,
                                                                "startDate": "2017-01-23",
                                                                "endDate": "2019-01-25",
                                                                "isCurrent": false,
                                                                "title": "경력1",
                                                                "description": ""
                                                            }
                                                        ]
                                                    }
                                                    """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 회원에 대한 경력 조회를 시도할 경우",
                            content = @Content(
                                    schema = @Schema(implementation = BasicErrorResponse.class),
                                    examples = @ExampleObject("""
                                            {
                                                "errorCode": "SP001",
                                                "statusCode": 40400,
                                                "message": "해당 회원이 존재하지 않습니다",
                                                "errorName": "MEMBER_NOT_FOUND",
                                                "statusCodeSeries": 4
                                            }
                                            """)
                            )
                    )
            }
    )
    public ResponseEntity<ApiBasicResponse<List<SpecDto>>> getSpecsOfSpecificMember(@PathVariable("memberId") Long memberId) {
        List<SpecDto> result = this.specService.getSpecsOfMember(memberId);
        return ResponseEntity.ok(
                ApiBasicResponse.of(result, HttpStatus.OK)
        );
    }

    @PutMapping("/members/{memberId}/specs")
    @Operation(
            summary = "경력 및 수상 이력 수정",
            parameters = @Parameter(
                    description = "회원 ID",
                    required = true,
                    example = "10005"
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                            SpecUpdateType에는 INSERT, UPDATE, DELETE가 있으며,
                            INSERT는 새로운 Spec 추가, UPDATE는 기존 Spec 수정, DELETE는 기존 Spec 삭제다.
                            1. INSERT의 경우 specId는 nullable이며 spec은 null이면 안 된다.
                            2. UPDATE의 경우 specId와 spec은 null이면 안 된다.
                            3. DELETE의 경우 spec은 nullable이며 specId는 null이면 안 된다.
                            """,
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SpecUpdateRequestDtoListSchema.class),
                            examples = @ExampleObject("""
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
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 업데이트 성공",
                            content = @Content(
                                    schema = @Schema(implementation = SpecUpdateResponseDtoSchema.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "statusCode": 200,
                                                "statusCodeSeries": 2,
                                                "content": {
                                                    "insertedSpecIds": [
                                                        99, 105
                                                    ],
                                                    "updatedSpecIds": [
                                                        100
                                                    ],
                                                    "deletedSpecIds": [
                                                        101
                                                    ]
                                                }
                                            }
                                            """,
                                            description = "insertedSpecIds는 추가된 Spec의 ID, updatedSpecIds는 수정된 Spec의 ID, deletedSpecIds는 삭제된 스펙의 ID")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 회원에 대한 경력 수정을 시도할 경우",
                            content = @Content(
                                    schema = @Schema(implementation = BasicErrorResponse.class),
                                    examples = @ExampleObject("""
                                            {
                                                "errorCode": "SP001",
                                                "statusCode": 40400,
                                                "message": "해당 회원이 존재하지 않습니다",
                                                "errorName": "MEMBER_NOT_FOUND",
                                                "statusCodeSeries": 4
                                            }
                                            """)
                            )
                    )
            }
    )
    public ResponseEntity<ApiBasicResponse<SpecUpdateResponseDto>> updateSpecs(@PathVariable("memberId") Long memberId,
                                                                               @RequestBody @Valid List<SpecUpdateRequestDto> dto) {
        SpecUpdateResponseDto result = this.specService.updateSpecsOfMember(memberId, dto);
        return ResponseEntity.ok(
                ApiBasicResponse.of(result, HttpStatus.OK)
        );
    }
}
