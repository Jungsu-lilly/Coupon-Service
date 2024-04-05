package com.example.couponcore.exception.common;

public class BaseException extends RuntimeException {
    int code;

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
    }
}
