package com.hf.healthfriend.domain.comment.controller;

import com.hf.healthfriend.domain.comment.dto.request.CommentCreationRequestDto;
import com.hf.healthfriend.domain.comment.dto.response.CommentCreationResponseDto;
import com.hf.healthfriend.domain.comment.service.CommentService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
}
