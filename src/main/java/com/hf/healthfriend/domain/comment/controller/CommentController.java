package com.hf.healthfriend.domain.comment.controller;

import com.hf.healthfriend.domain.comment.dto.CommentDto;
import com.hf.healthfriend.domain.comment.dto.request.CommentCreationRequestDto;
import com.hf.healthfriend.domain.comment.dto.response.CommentCreationResponseDto;
import com.hf.healthfriend.domain.comment.repository.dto.CommentUpdateDto;
import com.hf.healthfriend.domain.comment.service.CommentService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/hr")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    @Operation(
            summary = "댓글 작성", description = "{postId}에 해당하는 포스트에 댓글 작성",
            responses = {
                    @ApiResponse(responseCode = "201", description = "댓글 작성 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 POST, 혹은 존재하지 않는 회원")
            }
    )
    public ResponseEntity<ApiBasicResponse<CommentCreationResponseDto>> createComment(
            @PathVariable("postId") long postId,
            @RequestBody CommentCreationRequestDto requestDto) {
        CommentCreationResponseDto result = this.commentService.createComment(postId, requestDto);
        return ResponseEntity.created(UriComponentsBuilder.fromPath("/posts/{postId}/comments/{commentId}")
                        .buildAndExpand(Map.of("postId", postId, "commentId", result.getCommentId()))
                        .toUri())
                .body(ApiBasicResponse.of(result, HttpStatus.CREATED));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(
            summary = "댓글 삭제", description = "{commentId}에 해당하는 댓글 삭제",
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 댓글을 삭제하려고 할 경우")
            }
    )
    public ResponseEntity<ApiBasicResponse<Void>> deleteComment(@PathVariable("commentId") long commentId) {
        this.commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiBasicResponse.of(HttpStatus.OK));
    }

    @GetMapping("/posts/{postId}/comments")
    @Operation(
            summary = "댓글 목록 조회",
            description = "{postId}에 해당하는 포스트에 달린 댓글을 조회",
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "{postId}에 해당하는 댓글이 없을 경우")
            }
    )
    public ResponseEntity<ApiBasicResponse<List<CommentDto>>> findCommentsOfSpecificPost(@PathVariable("postId") long postId) {
        List<CommentDto> result = this.commentService.getCommentsOfPost(postId);
        log.info("result={}", result);
        return ResponseEntity.ok(ApiBasicResponse.of(result, HttpStatus.OK));
    }

    @GetMapping("/comments")
    @Operation(
            summary = "특정 회원의 댓글 목록 조회",
            description = "Query Parameter의 writerId에 해당하는 회원의 댓글 목록 조회",
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "Query Parameter에 writerId가 없을 때"),
                    @ApiResponse(responseCode = "404", description = "그런 회원 없을 때")
            }
    )
    public ResponseEntity<ApiBasicResponse<List<CommentDto>>> findCommentsOfSpecificMember(@RequestParam("writerId") long writerId) {
        List<CommentDto> result = this.commentService.getCommentsOfWriter(writerId);
        log.info("result={}", result);
        return ResponseEntity.ok(ApiBasicResponse.of(result, HttpStatus.OK));
    }

    @PatchMapping("/comments/{commentId}")
    @Operation(
            summary = "댓글 수정",
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
                    @ApiResponse(responseCode = "404", description = "commentId에 해당하는 댓글이 존재하지 않을 때")
            }
    )
    public ResponseEntity<ApiBasicResponse<CommentDto>> updateComment(@PathVariable("commentId") long commentId,
                                                                      @RequestBody CommentUpdateDto requestDto) {
        CommentDto updatedCommentDto = this.commentService.updateComment(commentId, requestDto);
        log.info("updatedCommentDto={}", updatedCommentDto);
        return ResponseEntity.ok(ApiBasicResponse.of(updatedCommentDto, HttpStatus.OK));
    }
}
