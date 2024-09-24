package com.hf.healthfriend.domain.like.entity;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Setter
    @Column(name = "is_canceled", nullable = false)
    private boolean canceled = false;

    public Like(Long likeId) {
        this.likeId = likeId;
    }

    public Like(Member member, Post post) {
        this.member = member;
        this.post = post;
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
