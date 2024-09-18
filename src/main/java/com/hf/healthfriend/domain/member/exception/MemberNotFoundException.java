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
    private Long memberId;
    private String loginId;

    public MemberNotFoundException(Long memberId) {
        super();
        this.memberId = memberId;
    }

    public MemberNotFoundException(String loginId) {
        super();
        this.loginId = loginId;
    }

    public MemberNotFoundException(Long memberId, String message) {
        super(message);
        this.memberId = memberId;
    }

    public MemberNotFoundException(String loginId, String message) {
        super(message);
        this.loginId = loginId;
    }

    public MemberNotFoundException(Long memberId, Throwable cause) {
        super(cause);
        this.memberId = memberId;
    }

    public MemberNotFoundException(String loginId, Throwable cause) {
        super(cause);
        this.loginId = loginId;
    }

    public MemberNotFoundException(Long memberId, String message, Throwable cause) {
        super(message, cause);
        this.memberId = memberId;
    }

    public MemberNotFoundException(String loginId, String message, Throwable cause) {
        super(message, cause);
        this.loginId = loginId;
    }
}
