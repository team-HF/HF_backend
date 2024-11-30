package com.hf.healthfriend.domain.wish.exception;

import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class WishException extends RuntimeException implements Supplier<WishException> {
    private WishErrorCode errorCode;
    private HttpStatus httpStatus;
    private String details;

    public WishException(WishErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    @Override
    public WishException get() {
        return new WishException(errorCode, httpStatus, details);
    }
}
