package com.example.couponcore.exception.common;

public class ConflictException extends BaseException {

    public ConflictException() {
        super(409, "data already exists.");
    }

    public ConflictException(String message) {
        super(409, message);
    }
}
