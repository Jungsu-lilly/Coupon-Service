package com.example.couponcore.exception.common;

public class DataNotFoundException extends BaseException {

    public DataNotFoundException() {
        super(404, "data not found");
    }

    public DataNotFoundException(String message) {
        super(404, message);
    }
}
