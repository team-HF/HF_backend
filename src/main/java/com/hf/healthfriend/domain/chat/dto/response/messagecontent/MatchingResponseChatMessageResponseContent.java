package com.hf.healthfriend.domain.chat.dto.response.messagecontent;

import com.hf.healthfriend.domain.chat.constant.MatchingResponseType;

public record MatchingResponseChatMessageResponseContent(
        MatchingResponseType matchingResponseType,
        String cancelMessage
) {
}
