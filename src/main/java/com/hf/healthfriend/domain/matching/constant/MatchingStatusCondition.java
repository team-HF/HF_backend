package com.hf.healthfriend.domain.matching.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum MatchingStatusCondition {
    ALL(List.of()),
    FINISHED(List.of(MatchingStatus.FINISHED)),
    IN_PROGRESS(List.of(MatchingStatus.ACCEPTED)),
    HALTED(List.of(MatchingStatus.REJECTED, MatchingStatus.UNEXPECTEDLY_HALTED));

    private final List<MatchingStatus> correspondingStatus;
}
