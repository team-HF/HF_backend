package com.hf.healthfriend.domain.matching.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
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

    @Schema(description = "null일 경우 쿠폰을 사용하지 않음. null이 아닐 경우 쿠폰을 사용해 매칭하는 것")
    private Long couponId;

    @NotNull
    @NotEmpty
    private String meetingPlace;

    @NotNull
    @NotEmpty
    private String meetingPlaceAddress;

    @NotNull
    @Future
    private LocalDateTime meetingTime;
}
