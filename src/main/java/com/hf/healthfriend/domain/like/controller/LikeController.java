package com.hf.healthfriend.domain.like.controller;

import com.hf.healthfriend.domain.like.controller.schema.LikeDtoListSchema;
import com.hf.healthfriend.domain.like.controller.schema.LikeDtoSchema;
import com.hf.healthfriend.domain.like.dto.LikeDto;
import com.hf.healthfriend.domain.like.service.LikeService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import com.hf.healthfriend.global.spec.schema.LongTypeSchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/hr")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/posts/{postId}/likes")
    @Operation(
            summary = "좋아요 추가",
            responses = {
                    @ApiResponse(
                            description = "좋아요 추가 성공",
                            responseCode = "201",
                            content = @Content(schema = @Schema(implementation = LongTypeSchema.class))
                    ),
                    @ApiResponse(
                            description = "postId 혹은 memberId가 없을 경우/좋아요 충복 추가 시 400 응답",
                            responseCode = "400",
                            content = @Content(schema = @Schema(implementation = BasicErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<ApiBasicResponse<Long>> addLike(@PathVariable("postId") Long postId,
                                                          @RequestParam("memberId") Long memberId) {
        Long generatedLikeId = this.likeService.addLike(memberId, postId);
        return new ResponseEntity<>(
                ApiBasicResponse.of(generatedLikeId, HttpStatus.CREATED),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/likes/{likeId}")
    @Operation(
            summary = "단일 좋아요 조회",
            responses = {
                    @ApiResponse(
                            description = "단일 좋아요 조회 성공",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = LikeDtoSchema.class))
                    ),
                    @ApiResponse(
                            description = "존재하지 않는 좋아요 정보 조회 시도",
                            responseCode = "404",
                            content = @Content(schema = @Schema(implementation = BasicErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<ApiBasicResponse<LikeDto>> getSingleLike(@PathVariable("likeId") Long likeId) {
        LikeDto responseDto = this.likeService.getLike(likeId);
        return new ResponseEntity<>(
                ApiBasicResponse.of(responseDto, HttpStatus.OK),
                HttpStatus.OK
        );
    }

    @GetMapping("/posts/{postId}/likes")
    @Operation(
            summary = "특정 글에 남겨진 좋아요 조회",
            responses = {
                    @ApiResponse(
                            description = "특정 글에 남겨진 좋아요 목록 조회 성공",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = LikeDtoListSchema.class))
                    )
            }
    )
    public ResponseEntity<ApiBasicResponse<List<LikeDto>>> getLikesOfSpecificPost(@PathVariable("postId") Long postId) {
        List<LikeDto> responseBody = this.likeService.getLikeOfPost(postId);
        return ResponseEntity.ok(
                ApiBasicResponse.of(
                        responseBody,
                        HttpStatus.OK
                )
        );
    }

    @GetMapping("/posts/{postId}/likes/count")
    @Operation(
            summary = "특정 글에 남겨진 좋아요 개수 조회",
            responses = {
                    @ApiResponse(
                            description = "좋아요 개수 조회 성공",
                            content = @Content(schema = @Schema(implementation = LongTypeSchema.class))
                    )
            }
    )
    public ResponseEntity<ApiBasicResponse<Long>> countLikesOfSinglePost(@PathVariable("postId") Long postId) {
        Long result = this.likeService.getLikeCountOfPost(postId);
        return ResponseEntity.ok(
                ApiBasicResponse.of(
                        result,
                        HttpStatus.OK
                )
        );
    }

    @GetMapping("/members/{memberId}/likes")
    @Operation(
            summary = "특정 회원이 남긴 좋아요 조회",
            responses = {
                    @ApiResponse(
                            description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = LikeDtoListSchema.class))
                    )
            }
    )
    public ResponseEntity<ApiBasicResponse<List<LikeDto>>> getLikesOfSingleMember(@PathVariable("memberId") Long memberId) {
        List<LikeDto> responseDto = this.likeService.getLikeOfMember(memberId);
        return ResponseEntity.ok(
                ApiBasicResponse.of(
                        responseDto,
                        HttpStatus.OK
                )
        );
    }

    @DeleteMapping("/likes/{likeId}")
    @Operation(
            summary = "좋아요 취소",
            responses = {
                    @ApiResponse(
                            description = "좋아요 취소 성공",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ApiBasicResponse.class))
                    ),
                    @ApiResponse(
                            description = "존재하지 않는 좋아요를 취소하려고 시도할 경우",
                            responseCode = "404",
                            content = @Content(schema = @Schema(implementation = BasicErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<ApiBasicResponse<Void>> cancelLike(@PathVariable("likeId") Long likeId) {
        this.likeService.cancelLike(likeId);
        return ResponseEntity.ok(
                ApiBasicResponse.of(HttpStatus.OK)
        );
    }
}
