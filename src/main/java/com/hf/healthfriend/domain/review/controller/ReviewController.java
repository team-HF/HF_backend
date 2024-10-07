package com.hf.healthfriend.domain.review.controller;

import com.hf.healthfriend.domain.review.dto.request.ReviewCreationRequestDto;
import com.hf.healthfriend.domain.review.service.ReviewService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import com.hf.healthfriend.global.spec.schema.LongTypeSchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hf")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/reviews")
    @Operation(
            summary = "리뷰 생성",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = ReviewCreationRequestDto.class),
                            examples = @ExampleObject("""
                                            {
                                              "matchingId": 10000,
                                              "reviewerId": 20000,
                                              "description": "샘플 리뷰",
                                              "score": 3,
                                              "evaluationType": "NOT_GOOD"
                                            }
                                            """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            description = "리뷰 생성 성공",
                            responseCode = "201",
                            content = @Content(
                                    schema = @Schema(implementation = LongTypeSchema.class),
                                    examples = @ExampleObject(
                                            name = "리뷰 생성 성공 시 Response Body",
                                            value = """
                                                    {
                                                      "statusCode": 201,
                                                      "statusCodeSeries": 2,
                                                      "message": "리뷰 생성 성공",
                                                      "content": 1000
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            description = "없는 회원 혹은 없는 매칭에 대해 리뷰를 남길 경우 실패",
                            responseCode = "400",
                            content = @Content(
                                    schema = @Schema(implementation = BasicErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "매칭이 존재하지 않음",
                                                    value = """
                                                            {
                                                                "statusCode": 40000,
                                                                "statusCodeSeries": 4,
                                                                "errorCode": "R001",
                                                                "errorName": "MATCHING_NOT_FOUND",
                                                                "message": "리뷰를 남기려는 매칭이 존재하지 않음 - matchingId=10000"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "회원이 존재하지 않음",
                                                    value = """
                                                            {
                                                                "statusCode": 40001,
                                                                "statusCodeSeries": 4,
                                                                "errorCode": "R002",
                                                                "errorName": "MEMBER_NOT_FOUND",
                                                                "message": "리뷰를 남기려는 회원이 존재하지 않음 - reviewerId=10000"
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<ApiBasicResponse<Long>> addReview(@RequestBody ReviewCreationRequestDto dto) {
        Long generatedReviewId = this.reviewService.addReview(dto);
        return new ResponseEntity<>(
                ApiBasicResponse.of(generatedReviewId, HttpStatus.CREATED, "리뷰 생성 성공"),
                HttpStatus.CREATED
        );
    }
}
