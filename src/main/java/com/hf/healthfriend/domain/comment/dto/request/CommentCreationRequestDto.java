package com.hf.healthfriend.domain.comment.dto.request;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class CommentCreationRequestDto {
    private long writerId;
    private String content;
}
