package com.hf.healthfriend.domain.post.service;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.post.dto.request.PostWriteRequest;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import com.hf.healthfriend.domain.post.service.PostService;
import com.hf.healthfriend.global.util.file.FileUrlResolver;
import com.hf.healthfriend.global.util.file.MultipartFileUploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

import static com.hf.healthfriend.domain.post.constant.PostCategory.GYM_RECOMMENDATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PostImageTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private FileUrlResolver fileUrlResolver;

    @Mock
    private MultipartFileUploader multipartFileUploader;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSavePostWithImage() throws IOException {
        // Given
        PostWriteRequest request = PostWriteRequest.builder()
                .category(GYM_RECOMMENDATION.name())
                .title("title")
                .content("content.")
                .writerId(1L)
                .build();

        Member member = new Member(1L);

        MockMultipartFile imageFile = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "test".getBytes()
        );

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(fileUrlResolver.generateFilePath(anyString(), anyString())).thenReturn("/files/image/test-image.jpg");
        when(fileUrlResolver.resolveFileUrl(anyString())).thenReturn("http://localhost/files/image/test-image.jpg");

        doNothing().when(multipartFileUploader).uploadFile(anyString(), any(MultipartFile.class));

        /**
         * thenAnswer : thenReturn보다 더 유동적으로 반환값 설정 가능
         * ReflectionTestUtils : Setter가 없어도 값 주입 가능
         */
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            ReflectionTestUtils.setField(post, "postId", 1L);
            return post;
        });

        // When
        Long postId = postService.save(request, imageFile);

        // Then
        assertEquals(1L, postId);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(multipartFileUploader, times(1)).uploadFile(anyString(), eq(imageFile));
    }
}