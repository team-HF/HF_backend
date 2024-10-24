package com.hf.healthfriend.domain.matching.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class MatchingRequestDto {
    private Long requesterId;
    private Long targetId;
    private String meetingPlace;
    private String meetingPlaceAddress;
    private LocalDateTime meetingTime;
}
