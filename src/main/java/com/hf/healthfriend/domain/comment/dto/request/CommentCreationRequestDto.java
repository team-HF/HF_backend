package com.hf.healthfriend.domain.comment.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class CommentCreationRequestDto {

    @NotNull
    private Long writerId;

    @NotEmpty
    private String content;

    @Nullable
    private Long parentCommentId;
}
