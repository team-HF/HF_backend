package com.hf.healthfriend.domain.matching.dto.response;

import com.hf.healthfriend.domain.matching.constant.MatchingStatus;

import java.time.LocalDateTime;

public record MatchingListResponseDto(
        Long matchingId,
        String meetingPlace,
        String meetingPlaceAddress,
        MatchingStatus matchingStatus,
        LocalDateTime meetingTime,
        LocalDateTime finishTime,
        ProfileOfMemberInMatchingResponseDto opponentInfo
) {
}
