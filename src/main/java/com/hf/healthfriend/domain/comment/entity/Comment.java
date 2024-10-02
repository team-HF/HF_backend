package com.hf.healthfriend.domain.comment.entity;

import com.hf.healthfriend.domain.BaseTimeEntity;
import com.hf.healthfriend.domain.like.entity.Like;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.post.entity.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();

    @Column(name = "content")
    private String content;

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDeleted = false;

    public Comment(@NotNull Post post, @NotNull Member writer, String content) {
        this.post = post;
        this.writer = writer;
        this.content = content;
        this.isDeleted = false;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateLastModified(LocalDateTime modifiedTime) {
        super.setLastModified(modifiedTime);
    }

    public void delete() {
        this.isDeleted = true;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", postId=" + post.getPostId() +
                ", writerId=" + writer.getId() +
                ", content='" + content + '\'' +
                ", creationTime=" + super.getCreationTime() +
                ", lastModified=" + super.getLastModified() +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
