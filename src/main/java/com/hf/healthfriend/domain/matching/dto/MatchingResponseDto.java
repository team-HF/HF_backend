package com.hf.healthfriend.domain.matching.dto;

import com.hf.healthfriend.domain.matching.constant.MatchingStatus;
import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.member.dto.SimpleMemberDto;
import com.hf.healthfriend.global.util.mapping.BeanMapping;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@BeanMapping(Matching.class)
@Builder
@Getter
@ToString
public class MatchingResponseDto {
    private Long matchingId;
    private SimpleMemberDto requester;
    private SimpleMemberDto requestTarget;
    private MatchingStatus status;
    private LocalDateTime meetingTime;
    private LocalDateTime creationTime;
    private LocalDateTime finishTime;

    public static MatchingResponseDto of(Matching entity) {
        return MatchingResponseDto.builder()
                .matchingId(entity.getMatchingId())
                .requester(SimpleMemberDto.of(entity.getRequester()))
                .requestTarget(SimpleMemberDto.of(entity.getTargetMember()))
                .status(entity.getStatus())
                .meetingTime(entity.getMeetingTime())
                .creationTime(entity.getCreationTime())
                .finishTime(entity.getFinishTime())
                .build();
    }
}
