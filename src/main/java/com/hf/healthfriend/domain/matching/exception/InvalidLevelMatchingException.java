package com.hf.healthfriend.domain.matching.exception;

import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import lombok.Getter;

@Getter
public class InvalidLevelMatchingException extends RuntimeException {
    private final FitnessLevel requesterLevel;
    private final FitnessLevel targetLevel;

    public InvalidLevelMatchingException(FitnessLevel requesterLevel,
                                         FitnessLevel targetLevel) {
        super();
        this.requesterLevel = requesterLevel;
        this.targetLevel = targetLevel;
    }
}
