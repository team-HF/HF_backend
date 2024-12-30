package com.hf.healthfriend.domain.chat.dto.response;

import com.hf.healthfriend.domain.matching.constant.MatchingStatus;
import lombok.Builder;

@Builder
public record ChatroomListResponseDto(
        Long chatroomId,
        Long opponentParticipantId,
        String opponentParticipantNickname,
        String opponentParticipantProfileImageUrl,
        MatchingStatus matchingStatus,
        String lastChatMessage,
        Integer unreadMessageCount
) {
}
