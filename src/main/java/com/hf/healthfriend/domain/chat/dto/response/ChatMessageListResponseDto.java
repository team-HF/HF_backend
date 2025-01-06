package com.hf.healthfriend.domain.chat.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ChatMessageListResponseDto(
        List<ChatMessageResponseDto> chatMessages,
        boolean isFirst,
        boolean isLast,
        int page,
        int pageSize
) {
}
