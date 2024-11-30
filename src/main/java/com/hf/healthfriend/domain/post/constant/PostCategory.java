package com.hf.healthfriend.domain.post.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostCategory {
    FREE_COMMUNITY("자유게시판"),
    COUNSELING("고민/상담");

    private final String categoryName;
}
