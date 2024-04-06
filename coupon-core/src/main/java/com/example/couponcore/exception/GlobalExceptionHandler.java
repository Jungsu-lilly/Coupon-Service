package com.example.couponcore.exception;

import com.example.couponcore.exception.common.BaseException;
import com.example.couponcore.exception.custom.CouponIssueException;
import com.example.couponcore.exception.response.BaseErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseErrorResponse> handleBaseException(BaseException exception) {
        return ResponseEntity
                .status(exception.getCode())
                .body(new BaseErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(CouponIssueException.class)
    public ResponseEntity<BaseErrorResponse> handleBaseException(CouponIssueException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BaseErrorResponse(exception.getMessage()));
    }
}
