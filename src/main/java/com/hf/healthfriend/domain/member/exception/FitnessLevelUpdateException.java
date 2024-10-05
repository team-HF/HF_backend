package com.hf.healthfriend.domain.member.exception;

import com.hf.healthfriend.domain.member.constant.FitnessLevel;

/**
 * 매칭 횟수가 10번 미만인 새싹이 고수로 변경하거나
 * 고수가 새싹으로 변경하려고 할 때 발생하는 예외
 */
public class FitnessLevelUpdateException extends RuntimeException {

    public FitnessLevelUpdateException(String message) {
        super(message);
    }

    public FitnessLevelUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public FitnessLevelUpdateException(Throwable cause) {
        super(cause);
    }
}
