package com.hf.healthfriend.domain.post.controller;

import com.hf.healthfriend.domain.post.dto.request.PostWriteRequest;
import com.hf.healthfriend.domain.post.dto.response.PostGetResponse;
import com.hf.healthfriend.domain.post.service.PostService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post API", description = "커뮤니티 API")
@RequiredArgsConstructor
@RequestMapping("/hf/")
@RestController
public class PostController {
    private final PostService postService;

    @Operation(summary = "글 작성", responses = {
            @ApiResponse(responseCode = "200", description = "작성 성공"),
            @ApiResponse(responseCode = "400", description = "작성 실패")
    })
    @PostMapping("/posts")
    public ResponseEntity<ApiBasicResponse<Long>> create(@Valid @RequestBody PostWriteRequest postWriteRequest, BearerTokenAuthentication authentication) {
        Long postId = postService.save(postWriteRequest, authentication);
        return ResponseEntity.ok(ApiBasicResponse.of(postId, HttpStatus.OK));
    }

    @Operation(summary = "글 수정", responses = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "수정 실패")
    })
    @PatchMapping("/posts/{postId}")
    public ResponseEntity<ApiBasicResponse<Long>> update(@Valid @RequestBody PostWriteRequest postWriteRequest,@PathVariable Long postId) {
        Long updatedPostId = postService.update(postWriteRequest,postId);
        return ResponseEntity.ok(ApiBasicResponse.of(updatedPostId, HttpStatus.OK));
    }

    @Operation(summary = "글 조회", responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "조회 실패")
    })
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiBasicResponse<PostGetResponse>> get(@PathVariable Long postId) {
        PostGetResponse postGetResponse = postService.get(postId);
        return ResponseEntity.ok(ApiBasicResponse.of(postGetResponse, HttpStatus.OK));
    }

    @Operation(summary = "글 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "삭제 실패")
    })
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiBasicResponse<Void>> delete(@PathVariable Long postId) {
        postService.delete(postId);
        return ResponseEntity.ok(ApiBasicResponse.of(HttpStatus.OK));
    }

}
