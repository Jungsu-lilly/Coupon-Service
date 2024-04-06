package com.example.couponcore.exception.response;

import lombok.Getter;

@Getter
public class BaseErrorResponse {
    private String error;

    public BaseErrorResponse(String error) {
        this.error = error;
    }
}
