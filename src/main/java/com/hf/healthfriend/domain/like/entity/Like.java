package com.hf.healthfriend.domain.like.entity;

import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.like.constant.LikeType;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좋아요 기능 관련 Entity 객체
 */
@Entity
@Table(name = "likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(name = "like_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LikeType likeType;

    @Column(name = "is_canceled", nullable = false)
    private boolean canceled = false;

    public Like(Long likeId) {
        this.likeId = likeId;
    }

    public Like(Member member, Post post, LikeType likeType) {
        this.member = member;
        this.post = post;
        this.likeType = likeType;
    }

    public Like(Member member, Comment comment, LikeType likeType) {
        this.member = member;
        this.comment = comment;
        this.likeType = likeType;
    }

    public void uncancel() {
        this.canceled = false;
    }

    public void cancel() {
        this.canceled = true;
    }

    @Override
    public String toString() {
        return "Like{" +
                "likeId=" + likeId +
                ", memberId=" + member.getId() +
                ", postId=" + post.getPostId() +
                ", canceled=" + canceled +
                '}';
    }
}
