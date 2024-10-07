package com.hf.healthfriend.domain.matching.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MatchingNotFoundException extends RuntimeException {
    private final Long matchingId;
}
