package com.hf.healthfriend.domain.coupon.controller;

import com.hf.healthfriend.domain.coupon.constant.CouponFetchType;
import com.hf.healthfriend.domain.coupon.controller.schema.CouponResponseSchema;
import com.hf.healthfriend.domain.coupon.dto.response.CouponResponseDto;
import com.hf.healthfriend.domain.coupon.service.CouponService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hf")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @GetMapping("/members/{memberId}/coupons")
    @Operation(summary = "회원에게 발급된 쿠폰 목록 조회")
    @ApiResponses({
            @ApiResponse(
                    description = "쿠폰 목록 조회 성공",
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = CouponResponseSchema.class),
                            examples = @ExampleObject("""
                                    {
                                        "statusCode": 200,
                                        "statusCodeSeries": 2,
                                        "content": [
                                            {
                                                "couponId": 1000,
                                                "receiverId": 500,
                                                "couponType": "MATCHING_COUPON",
                                                "expirationTime": "2024-11-14T08:36:27.830Z",
                                                "validPeriodInDays": 2,
                                                "grantTime": "2024-11-12T08:36:27.830Z",
                                                "used": false,
                                                "read": false,
                                                "expired": false,
                                                "achievedLevel": 3,
                                                "grantedMatchingCount": 2,
                                                "leftMatchingCount": 2
                                            }
                                        ]
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<ApiBasicResponse<List<CouponResponseDto>>> getCoupons(
            @PathVariable("memberId") Long memberId,
            @RequestParam("fetchType") CouponFetchType couponFetchType
    ) {
        List<CouponResponseDto> result = this.couponService.getCoupons(memberId, couponFetchType);
        return ResponseEntity.ok(
                ApiBasicResponse.of(result, HttpStatus.OK)
        );
    }
}
