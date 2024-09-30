package com.hf.healthfriend.domain.post.controller;

import com.hf.healthfriend.domain.post.dto.request.PostWriteRequest;
import com.hf.healthfriend.domain.post.dto.response.PostGetResponse;
import com.hf.healthfriend.domain.post.dto.response.PostListObject;
import com.hf.healthfriend.domain.post.service.PostService;
import com.hf.healthfriend.domain.post.service.ViewService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Post API", description = "커뮤니티 API")
@RequiredArgsConstructor
@RequestMapping("/hf/")
@RestController
public class PostController {
    private final PostService postService;
    private final ViewService viewService;

    @Operation(summary = "글 작성", responses = {
            @ApiResponse(responseCode = "200", description = "작성 성공"),
            @ApiResponse(responseCode = "400", description = "작성 실패")
    })
    @PostMapping("/posts")
    public ResponseEntity<ApiBasicResponse<Long>> create(@Valid @RequestBody PostWriteRequest postWriteRequest) {
        Long postId = postService.save(postWriteRequest);
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
    public ResponseEntity<ApiBasicResponse<PostGetResponse>> get(@PathVariable Long postId, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String visitCookieValue = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("visit")) {
                    visitCookieValue = cookie.getValue();
                    break;
                }
            }
        }
        boolean canUpdateViewCount = viewService.canAddViewCount(visitCookieValue,postId);
        if(canUpdateViewCount){
            String updatedCookieValue = viewService.addPostIdToVisitCookie(visitCookieValue,postId);
            Cookie newCookie = new Cookie("visit", updatedCookieValue);
            newCookie.setPath("/");
            newCookie.setMaxAge(60*60*24);
            newCookie.setHttpOnly(true);
            response.addCookie(newCookie);
        }
        PostGetResponse postGetResponse = postService.get(postId,canUpdateViewCount);
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

    @Operation(summary = "목록 조회", responses = {
            @ApiResponse(responseCode = "200", description = "목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "목록 조회 실패")
    })
    @GetMapping("/list")
    public ResponseEntity<ApiBasicResponse<List<PostListObject>>> getList(
            @RequestParam(value = "page", defaultValue = "1") int page) {
        return ResponseEntity.ok(ApiBasicResponse.of(postService.getList(page),HttpStatus.OK));
    }

    @Operation(summary = "검색 목록 조회", responses = {
            @ApiResponse(responseCode = "200", description = "검색 조회 성공"),
            @ApiResponse(responseCode = "400", description = "검색 조회 실패")
    })
    @GetMapping("/list/search")
    public ResponseEntity<ApiBasicResponse<List<PostListObject>>> getSearchList(
            @RequestParam(value = "page", defaultValue = "1") int page,@RequestParam String keyword) {
        return ResponseEntity.ok(ApiBasicResponse.of(postService.getsearchedList(page,keyword),HttpStatus.OK));
    }


}
