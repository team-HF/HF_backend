package com.hf.healthfriend.domain.wish.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum
WishErrorCode {
    FORBIDDEN_WISH_DELETE(40301, "WISH001", "찜을 삭제할 권한이 없습니다."),

    WISH_NOT_FOUND(40401, "WISH002", "존재하지 않는 찜입니다."),

    DUPLICATE_WISH(40402, "WISH003", "이미 존재하는 찜입니다."),
    MEMBER_NOT_FOUND(40403,"WISH004","존재하지 않는 멤버입니다. ");
    private final int statusCode;
    private final String errorCode;
    private final String message;

    public int statusCode() {return this.statusCode;}

    public String errorCode() {
        return this.errorCode;
    }

    public String message() {
        return this.message;
    }

}
