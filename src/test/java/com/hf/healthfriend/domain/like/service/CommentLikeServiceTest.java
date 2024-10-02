package com.hf.healthfriend.domain.like.service;

import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.repository.CommentRepository;
import com.hf.healthfriend.domain.like.constant.LikeType;
import com.hf.healthfriend.domain.like.dto.CommentLikeDto;
import com.hf.healthfriend.domain.like.entity.Like;
import com.hf.healthfriend.domain.like.exception.CommentOrMemberNotExistsException;
import com.hf.healthfriend.domain.like.exception.DuplicateCommentLikeException;
import com.hf.healthfriend.domain.like.repository.LikeRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CommentLikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private LikeService likeService;

    private Member testMember;
    private Comment testComment;

    @BeforeEach
    public void setup() {
        testMember = new Member(1L);
        testComment = Comment.builder()
                .commentId(1L)
                .writer(testMember)
                .content("Test Comment")
                .build();
    }

    @DisplayName("특정 회원이 특정 댓글에 좋아요를 추가하는 테스트")
    @Test
    public void addCommentLike_Success() {
        // Given
        when(likeRepository.findByMemberIdAndCommentId(testMember.getId(), testComment.getCommentId())).thenReturn(Optional.empty());

        Like savedLike = new Like(1L,
                new Member(testMember.getId()),
                Comment.builder().commentId(testComment.getCommentId()).build(),
                LikeType.COMMENT);
        when(likeRepository.save(any(Like.class))).thenReturn(savedLike);

        // When
        Long likeId = likeService.addCommentLike(testMember.getId(), testComment.getCommentId());

        // Then
        assertThat(likeId).isEqualTo(1L);
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @DisplayName("이미 존재하는 좋아요를 추가하려고 할 때 예외가 발생하는지 테스트")
    @Test
    public void addCommentLike_DuplicateLikeException() {
        // Given: 특정 회원이 특정 댓글에 이미 좋아요를 남겼을 때
        Like existingLike = new Like(1L,
                new Member(testMember.getId()),
                Comment.builder().commentId(testComment.getCommentId()).build(),
                LikeType.COMMENT);
        when(likeRepository.findByMemberIdAndCommentId(testMember.getId(), testComment.getCommentId())).thenReturn(Optional.of(existingLike));

        // When & Then
        assertThrows(DuplicateCommentLikeException.class, () -> {
            likeService.addCommentLike(testMember.getId(), testComment.getCommentId());
        });
        verify(likeRepository, times(0)).save(any(Like.class));
    }

    @DisplayName("댓글이나 회원이 존재하지 않을 때 예외가 발생하는지 테스트")
    @Test
    public void addCommentLike_CommentOrMemberNotExistsException() {
        // Given
        when(likeRepository.findByMemberIdAndCommentId(testMember.getId(), testComment.getCommentId())).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenThrow(new DataIntegrityViolationException("Integrity constraint violation"));

        // When & Then
        assertThrows(CommentOrMemberNotExistsException.class, () -> {
            likeService.addCommentLike(testMember.getId(), testComment.getCommentId());
        });
    }


    @DisplayName("특정 댓글의 좋아요 목록을 가져오는지 테스트")
    @Test
    public void getLikesOfSpecificComment_Success() {
        // Given: 특정 댓글에 좋아요가 존재할 때
        Like like = new Like(1L,
                new Member(testMember.getId()),
                Comment.builder().commentId(testComment.getCommentId()).build(),
                LikeType.COMMENT);
        when(likeRepository.findByCommentId(testComment.getCommentId())).thenReturn(List.of(like));

        // When
        List<CommentLikeDto> likes = likeService.getLikeOfComment(testComment.getCommentId());

        // Then
        assertThat(likes.size()).isEqualTo(1);
        assertThat(likes.get(0).getCommentId()).isEqualTo(testComment.getCommentId());
        assertThat(likes.get(0).getMemberId()).isEqualTo(testMember.getId());
        verify(likeRepository, times(1)).findByCommentId(testComment.getCommentId());
    }
}
