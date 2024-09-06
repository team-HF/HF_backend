package com.hf.healthfriend.domain.member.exception;

import lombok.Getter;
import lombok.ToString;

/**
 * 주어진 ID에 해당하는 회원이 데이터베이스에 없을 때 던져지는 Exception
 *
 * @author PGD
 * @see com.hf.healthfriend.domain.member.service.MemberService
 */
@Getter
@ToString
public class MemberNotFoundException extends RuntimeException {
    private final String memberId;

    public MemberNotFoundException(String memberId) {
        super();
        this.memberId = memberId;
    }

    public MemberNotFoundException(String memberId, String message) {
        super(message);
        this.memberId = memberId;
    }

    public MemberNotFoundException(String memberId, Throwable cause) {
        super(cause);
        this.memberId = memberId;
    }

    public MemberNotFoundException(String memberId, String message, Throwable cause) {
        super(message, cause);
        this.memberId = memberId;
    }
}
