package com.example.couponcore.exception.custom;

import com.example.couponcore.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CouponIssueException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;


    public CouponIssueException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "[%s] %s".formatted(errorCode, message);
    }
}
