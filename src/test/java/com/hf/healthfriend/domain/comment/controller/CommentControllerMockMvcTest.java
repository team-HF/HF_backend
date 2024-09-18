package com.hf.healthfriend.domain.comment.controller;

import com.hf.healthfriend.domain.comment.dto.request.CommentCreationRequestDto;
import com.hf.healthfriend.domain.comment.dto.response.CommentCreationResponseDto;
import com.hf.healthfriend.domain.comment.service.CommentService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockBean(JpaMetamodelMappingContext.class)
class CommentControllerMockMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CommentService commentService;

    @BeforeEach
    void beforeEach() {
        when(this.commentService.createComment(
                        10000L,
                        CommentCreationRequestDto.builder()
                                .content("sample-content")
                                .writerId(1000L)
                                .build()
                )
        )
                .thenReturn(
                        CommentCreationResponseDto.builder()
                                .commentId(10000L)
                                .creationTime(LocalDateTime.now())
                                .postId(10000L)
                                .writerId(1000L)
                                .content("sample-content")
                                .build()
                );
    }

    @DisplayName("POST /posts/{postId}/comments - create a comment - 201 expected")
    @Test
    void createComment_success() throws Exception {
        MockHttpServletResponse response = this.mockMvc.perform(post("/hr/posts/{postId}/comments", 10000L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "writerId": 1000,
                                    "content": "sample-content"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(content().json("""
                        {
                            "statusCode": 201,
                            "statusCodeSeries": 2,
                            "content": {
                                "postId": 10000,
                                "writerId": 1000,
                                "content": "sample-content"
                            }
                        }
                        """))
                .andReturn()
                .getResponse();
        log.info("Response status={}", response.getStatus());
        log.info("Response Location Header={}", response.getHeader(HttpHeaders.LOCATION));
        log.info("Response body:\n{}", response.getContentAsString());
    }

    @SneakyThrows
    @DisplayName("DELETE /comments/{commentId} - 삭제 성공")
    @Test
    void deleteComment_success() {
        CommentCreationRequestDto requestDto = CommentCreationRequestDto.builder()
                .content("sample-content")
                .writerId(1000L)
                .build();
        CommentCreationResponseDto creationResult = this.commentService.createComment(10000L, requestDto);


        this.mockMvc.perform(delete("/hr/comments/{commentId}", creationResult.getCommentId()))
                .andExpect(status().isOk());
    }
}