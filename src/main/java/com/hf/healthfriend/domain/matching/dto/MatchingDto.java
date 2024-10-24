package com.hf.healthfriend.domain.matching.dto;

import com.hf.healthfriend.domain.matching.constant.MatchingStatus;
import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.global.util.mapping.BeanMapping;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@BeanMapping(Matching.class)
@Getter
@ToString
public class MatchingDto {
    private Long matchingId;
    private MemberDto targetMember;
    private MatchingStatus status;
    private LocalDateTime meetingTime;
    private LocalDateTime creationTime;
    private LocalDateTime finishTime;

    public void setTargetMember(MemberDto targetMember) {
        if (this.targetMember != null) {
            throw new UnsupportedOperationException("targetMember가 이미 설정되었습니다");
        }
        this.targetMember = targetMember;
    }
}
