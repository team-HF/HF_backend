package com.hf.healthfriend.domain.matching.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class MatchingRequestDto {

    @NotNull
    private Long requesterId;
    
    @NotNull
    private Long targetId;

    @NotNull
    @Future
    private LocalDateTime meetingTime;
}
