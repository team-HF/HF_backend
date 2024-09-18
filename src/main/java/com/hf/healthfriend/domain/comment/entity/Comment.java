package com.hf.healthfriend.domain.comment.entity;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.post.entity.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class Comment {

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

    @Column(name = "content")
    private String content;

    @Column(name = "creation_time")
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    private LocalDateTime creationTime = LocalDateTime.now();

    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    private LocalDateTime lastModified = null;

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDeleted = false;

    public Comment(@NotNull Post post, @NotNull Member writer, String content) {
        this.post = post;
        this.writer = writer;
        this.content = content;
        this.creationTime = LocalDateTime.now();
        this.isDeleted = false;
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
                ", creationTime=" + creationTime +
                ", lastModified=" + lastModified +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
