package com.hf.healthfriend.domain.notification.service;

import com.hf.healthfriend.domain.notification.constant.NotificationType;
import com.hf.healthfriend.domain.notification.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.repository.CommentJpaRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.notification.exception.NotificationErrorCode;
import com.hf.healthfriend.domain.notification.exception.NotificationException;
import com.hf.healthfriend.domain.notification.publisher.NotificationPublisher;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {
    private final PostRepository postRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final NotificationPublisher notificationPublisher;

    public void publishPostLikeNot(Long memberId, Long postId) {
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new NotificationException(NotificationErrorCode.POST_NOT_FOUND));
        String actorNickname = post.getMember().getNickname();
        notificationPublisher.publishNotification(
                memberId,
                NotificationType.ADD_LIKE_TO_POST,
                actorNickname,
                postId
        );
        log.info("글에 달린 좋아요 알림 전송 postId : {}, memberId : {}, actor : {}"
                , post.getPostId(),memberId, actorNickname);
    }

    public void publishCommentLikeNot(Long memberId, Long commentId) {
        Post post = commentJpaRepository.findPostByCommentIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new NotificationException(NotificationErrorCode.COMMENT_NOT_FOUND));
        String actorNickname = post.getMember().getNickname();
        notificationPublisher.publishNotification(
                memberId,
                NotificationType.ADD_LIKE_TO_COMMENT,
                actorNickname,
                post.getPostId()
        );
        log.info("댓글에 달린 좋아요 알림 전송 postId : {}, memberId : {}, actor : {}"
                , post.getPostId(),memberId, actorNickname);
    }

    public void publishCommentNot(Comment parentComment, Post post, Member writer) {
        if (parentComment == null) {
            notificationPublisher.publishNotification(
                    post.getMember().getId(),
                    NotificationType.ADD_COMMENT_TO_POST,
                    writer.getNickname(),
                    post.getPostId()
            );
            log.info("글에 달린 댓글 알림 전송 postId: {}, memberId: {}, actor: {}"
                    ,post.getPostId(),post.getMember().getId(),writer.getNickname());
        }
        else{
            notificationPublisher.publishNotification(
                    parentComment.getWriter().getId(),
                    NotificationType.ADD_COMMENT_TO_COMMENT,
                    writer.getNickname(),
                    post.getPostId()
            );
            log.info("댓글에 달린 댓글 알림 전송 commentId: {}, memberId: {}, actor: {}"
                    ,post.getPostId(),parentComment.getWriter().getId(),writer.getNickname());        }
    }
}
