package com.hf.healthfriend.domain.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DuplicateMemberCreationException extends RuntimeException {
    private final String memberId;
}
