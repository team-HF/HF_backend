package com.hf.healthfriend.domain.post.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PostErrorCode {
    INVALID_REQUEST_FORMAT(40003,"P001","글 형식이 잘못되었습니다."),
    NON_EXIST_POST(40400,"P002","존재하지 않는 글입니다."),
    FORBIDDEN_UPDATE(40300,"P003","해당 게시글을 수정할 권한이 없습니다."),
    FORBIDDEN_DELETE(40301,"P004","해당 게시글을 삭제할 권한이 없습니다.");


    private int status;
    private String code;
    private String message;
    private String detail;

    PostErrorCode(int status, String code, String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getKey() {
        return this.code;
    }

    public String getValue() {
        return this.message;
    }

}
