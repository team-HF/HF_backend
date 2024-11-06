package com.hf.healthfriend.domain.post.service;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.post.dto.request.PostWriteRequest;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import java.util.Optional;
import org.springframework.test.util.ReflectionTestUtils;

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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSavePostWithImage() {
        // Given
        PostWriteRequest request = PostWriteRequest.builder()
                .category(GYM_RECOMMENDATION.name())
                .title("title")
                .content("content.")
                .writerId(1L)
                .build();

        Member member = new Member(1L);

        MockMultipartFile imageFile = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "Test Image Content".getBytes()
        );

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        // thenAnswer은 호출된 메서드의 인자에 따라 동적으로 값을 반환한다
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            //ReflectionTestUtils를 사용하면 setter 없이도 id 값을 설정할 수 있다.
            ReflectionTestUtils.setField(post, "postId", 1L);
            return post;
        });
        // When
        Long postId = postService.save(request, imageFile);

        // Then
        assertEquals(1L, postId);
        verify(postRepository, times(1)).save(any(Post.class));
    }
}