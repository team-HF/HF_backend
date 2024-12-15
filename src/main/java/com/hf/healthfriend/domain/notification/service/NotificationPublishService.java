package com.hf.healthfriend.domain.notification.service;

import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.notification.constant.NotificationType;
import lombok.RequiredArgsConstructor;
import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.repository.CommentJpaRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.notification.exception.NotificationErrorCode;
import com.hf.healthfriend.domain.notification.exception.NotificationException;
import com.hf.healthfriend.domain.notification.publisher.NotificationPublisher;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Slf4j
public class NotificationPublishService {
    private final PostRepository postRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final MemberRepository memberRepository;
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

    public void publishPopularPostNot(Long postId){
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new NotificationException(NotificationErrorCode.POST_NOT_FOUND));
        notificationPublisher.publishNotification(
                post.getMember().getId(),
                NotificationType.POPULAR_POST,
                null,
                postId
        );
        log.info("인기글 선정 알림 전송 postId : {}, memberId : {}"
                , post.getPostId(),post.getMember().getId());
    }

    public void publishReviewNot(Long reviewerId, Long revieweeId, Long reviewId) {
        Member actor = memberRepository.findByMemberId(reviewerId)
                        .orElseThrow(() -> new NotificationException(NotificationErrorCode.MEMBER_NOT_FOUND));
        notificationPublisher.publishNotification(
                revieweeId,
                NotificationType.MATCH_END_REVIEW,
                actor.getNickname(),
                reviewId

        );
        log.info("리뷰 작성 알림 전송 reviewerId : {}, actor : {}, reviewId : {}",
                reviewerId, actor.getNickname(), reviewId);
    }
    // TODO : 채팅 개발 완료시 targetId 채팅방 아이디로 수정
    public void publishMatchRequestNot(Matching matching){
        notificationPublisher.publishNotification(
                matching.getTargetMember().getId(),
                NotificationType.MATCH_REQUEST,
                matching.getTargetMember().getNickname(),
                null
        );
        log.info("매칭 신청 알림 전송 reviewerId : {}, actor : {}",
                matching.getTargetMember().getId(), matching.getTargetMember().getNickname());
    }

    public void publishMatchAcceptNot(Matching matching){
        notificationPublisher.publishNotification(
                matching.getTargetMember().getId(),
                NotificationType.MATCH_ACCEPT,
                matching.getTargetMember().getNickname(),
                null
        );
        log.info("매칭 수락 알림 전송 reviewerId : {}, actor : {}",
                matching.getTargetMember().getId(), matching.getTargetMember().getNickname());
    }

    public void publishMatchRejectNot(Matching matching){
        notificationPublisher.publishNotification(
                matching.getTargetMember().getId(),
                NotificationType.MATCH_REJECT,
                matching.getTargetMember().getNickname(),
                null
        );
        log.info("매칭 거절 알림 전송 reviewerId : {}, actor : {}",
                matching.getTargetMember().getId(), matching.getTargetMember().getNickname());
    }
    // TODO : 매칭 펑크 기능 개발시 이어서 작업
    public void publishMatchPunkNot(Matching matching){
        notificationPublisher.publishNotification(
                matching.getTargetMember().getId(),
                NotificationType.MATCH_PUNK,
                matching.getTargetMember().getNickname(),
                null
        );
        log.info("매칭 펑크 알림 전송 reviewerId : {}, actor : {}",
                matching.getTargetMember().getId(), matching.getTargetMember().getNickname());
    }


}
