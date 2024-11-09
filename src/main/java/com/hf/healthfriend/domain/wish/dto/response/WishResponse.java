package com.hf.healthfriend.domain.wish.dto.response;

import lombok.Builder;

@Builder
public record WishResponse(
        Long wishedId,
        Long wisherId
) {
}
