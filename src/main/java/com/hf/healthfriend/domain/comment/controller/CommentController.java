package com.hf.healthfriend.domain.comment.controller;

import com.hf.healthfriend.domain.comment.dto.CommentDto;
import com.hf.healthfriend.domain.comment.dto.request.CommentCreationRequestDto;
import com.hf.healthfriend.domain.comment.dto.response.CommentCreationResponseDto;
import com.hf.healthfriend.domain.comment.repository.dto.CommentUpdateDto;
import com.hf.healthfriend.domain.comment.service.CommentService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
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
    public ResponseEntity<ApiBasicResponse<Void>> deleteComment(@PathVariable("commentId") long commentId) {
        this.commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiBasicResponse.of(HttpStatus.OK));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiBasicResponse<List<CommentDto>>> findCommentsOfSpecificPost(@PathVariable("postId") long postId) {
        List<CommentDto> result = this.commentService.getCommentsOfPost(postId);
        log.info("result={}", result);
        return ResponseEntity.ok(ApiBasicResponse.of(result, HttpStatus.OK));
    }

    @GetMapping("/comments")
    public ResponseEntity<ApiBasicResponse<List<CommentDto>>> findCommentsOfSpecificMember(@RequestParam("writerId") long writerId) {
        List<CommentDto> result = this.commentService.getCommentsOfWriter(writerId);
        log.info("result={}", result);
        return ResponseEntity.ok(ApiBasicResponse.of(result, HttpStatus.OK));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<ApiBasicResponse<CommentDto>> updateComment(@PathVariable("commentId") long commentId,
                                                                      @RequestBody CommentUpdateDto requestDto) {
        CommentDto updatedCommentDto = this.commentService.updateComment(commentId, requestDto);
        log.info("updatedCommentDto={}", updatedCommentDto);
        return ResponseEntity.ok(ApiBasicResponse.of(updatedCommentDto, HttpStatus.OK));
    }
}
