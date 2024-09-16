package com.hf.healthfriend.domain.post.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostCategory {
    GYM_RECOMMENDATION("헬스장 추천"),
    WORKOUT_CERTIFICATION("운동 인증"),
    COUNSELING("고민/상담");

    private final String categoryName;
}
