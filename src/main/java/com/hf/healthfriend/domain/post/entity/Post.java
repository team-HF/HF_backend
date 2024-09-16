package com.hf.healthfriend.domain.post.entity;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.post.constant.PostCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Description;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @NotNull
    private String title;

    @Enumerated(EnumType.STRING)
    private PostCategory category;

    private LocalDateTime creationTime;
    private LocalDateTime lastModified;

    @Builder.Default
    private Long viewCount = 0L;


    @Description("삭제한 post")
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void delete(){
        this.isDeleted=true;
    }


}
