package com.hf.healthfriend.domain.comment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hf.healthfriend.domain.comment.dto.request.CommentCreationRequestDto;
import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.repository.CommentRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.notification.constant.NotificationType;
import com.hf.healthfriend.domain.notification.service.NotificationService;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentNotificationTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("글에 대한 댓글 알림")
    void testSendCommentNotification_ForPostComment() {
        // Given
        long postId = 1L;
        long writerId = 2L;
        long postWriterId = 3L;

        Member writer = Member.builder()
                .id(writerId)
                .nickname("액터")
                .build();
        Post post = Post.builder()
                .postId(postId)
                .member(new Member(postWriterId))
                .build();

        Comment savedComment = Comment.builder()
                .commentId(10L)
                .post(post)
                .writer(writer)
                .build();

        CommentCreationRequestDto requestDto = new CommentCreationRequestDto(writerId, "comment", null);


        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
        when(memberRepository.findByMemberId(writerId)).thenReturn(Optional.ofNullable(writer));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // When
        commentService.createComment(postId, requestDto);
        // Then
        verify(notificationService).publishNotification(
                eq(3L),
                eq(NotificationType.ADD_COMMENT_TO_POST),
                eq("액터"),
                eq(postId)
        );
    }
}
