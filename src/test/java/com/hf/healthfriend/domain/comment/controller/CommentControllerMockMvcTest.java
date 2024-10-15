package com.hf.healthfriend.domain.comment.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hf.healthfriend.domain.comment.constant.CommentSortType;
import com.hf.healthfriend.domain.comment.dto.CommentDto;
import com.hf.healthfriend.domain.comment.dto.request.CommentCreationRequestDto;
import com.hf.healthfriend.domain.comment.dto.response.CommentCreationResponseDto;
import com.hf.healthfriend.domain.comment.service.CommentService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        when(this.commentService.getCommentsOfPost(10000L, CommentSortType.LATEST))
                .thenReturn(
                        List.of(
                                CommentDto.builder()
                                        .commentId(1L)
                                        .postId(10000L)
                                        .writerId(100L)
                                        .content("sample1")
                                        .creationTime(LocalDateTime.now())
                                        .build(),
                                CommentDto.builder()
                                        .commentId(2L)
                                        .postId(10000L)
                                        .writerId(100L)
                                        .content("sample2")
                                        .creationTime(LocalDateTime.now())
                                        .build(),
                                CommentDto.builder()
                                        .commentId(3L)
                                        .postId(10000L)
                                        .writerId(101L)
                                        .content("sample3")
                                        .creationTime(LocalDateTime.now())
                                        .build()
                        )
                );
        when(this.commentService.getCommentsOfWriter(10000L))
                .thenReturn(
                        List.of(
                                CommentDto.builder()
                                        .commentId(1L)
                                        .postId(1000L)
                                        .writerId(10000L)
                                        .content("sample1")
                                        .creationTime(LocalDateTime.now())
                                        .build(),
                                CommentDto.builder()
                                        .commentId(2L)
                                        .postId(1000L)
                                        .writerId(10000L)
                                        .content("sample2")
                                        .creationTime(LocalDateTime.now())
                                        .build(),
                                CommentDto.builder()
                                        .commentId(3L)
                                        .postId(1001L)
                                        .writerId(10000L)
                                        .content("sample3")
                                        .creationTime(LocalDateTime.now())
                                        .build()
                        )
                );
    }

    @DisplayName("POST /posts/{postId}/comments - create a comment - 201 expected")
    @Test
    void createComment_success() throws Exception {
        MockHttpServletResponse response = this.mockMvc.perform(post("/hf/posts/{postId}/comments", 10000L)
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


        this.mockMvc.perform(delete("/hf/comments/{commentId}", creationResult.commentId()))
                .andExpect(status().isOk());
    }

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("GET /posts/{postId}/comments - 조회 성공")
    @Test
    void findCommentsOfSpecificPost() throws Exception {
        String responseBodyAsString = this.mockMvc.perform(get("/hf/posts/{postId}/comments", 10000L)
                        .param("sortType", "LATEST")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        log.info("responseBodyAsString:\n{}", responseBodyAsString);
        ApiBasicResponse<List<CommentDto>> responseBody = this.objectMapper.readValue(responseBodyAsString, new TypeReference<ApiBasicResponse<List<CommentDto>>>() {
        });

        assertThat(responseBody.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseBody.getStatusCodeSeries()).isEqualTo(HttpStatus.OK.series().value());
        assertThat(responseBody.getContent()).size().isEqualTo(3);
    }


    @DisplayName("GET /comments?writerId= - 조회 성공")
    @Test
    void findCommentsOfSpecificWriter() throws Exception {
        String responseBodyAsString = this.mockMvc.perform(get("/hf/comments")
                        .param("writerId", String.valueOf(10000))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        log.info("responseBodyAsString:\n{}", responseBodyAsString);
        ApiBasicResponse<List<CommentDto>> responseBody = this.objectMapper.readValue(responseBodyAsString, new TypeReference<ApiBasicResponse<List<CommentDto>>>() {
        });

        assertThat(responseBody.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseBody.getStatusCodeSeries()).isEqualTo(HttpStatus.OK.series().value());
        assertThat(responseBody.getContent()).size().isEqualTo(3);
    }
}