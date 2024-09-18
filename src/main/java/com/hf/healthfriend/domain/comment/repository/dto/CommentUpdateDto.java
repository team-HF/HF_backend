package com.hf.healthfriend.domain.comment.repository.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class CommentUpdateDto {
    private String content;
}
