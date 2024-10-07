package com.hf.healthfriend.domain.matching.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MatchingNotFoundException extends RuntimeException {
    private final Long matchingId;
}
