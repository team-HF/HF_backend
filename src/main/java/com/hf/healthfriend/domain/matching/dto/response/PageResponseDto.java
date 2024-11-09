package com.hf.healthfriend.domain.matching.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PageResponseDto<C>(
        int page,
        int pageSize,
        long totalElementCount,
        int totalPageCount,
        List<C> content
) {
}
