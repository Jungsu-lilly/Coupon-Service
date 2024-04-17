package com.example.couponcore.repository.redis;

import com.example.couponcore.exception.ErrorCode;
import com.example.couponcore.exception.common.BaseException;
import com.example.couponcore.exception.common.ConflictException;
import com.example.couponcore.exception.custom.CouponIssueException;

public enum CouponIssueRequestCode {
    SUCCESS(1),
    DUPLICATED_COUPON_ISSUE(2),
    INVALID_COUPON_ISSUE_QUANTITY(3);

    CouponIssueRequestCode(int code) {

    }

    public static CouponIssueRequestCode find(String code) {
        int codeValue = Integer.parseInt(code);
        if (codeValue == 1) return SUCCESS;
        if (codeValue == 2) return DUPLICATED_COUPON_ISSUE;
        if (codeValue == 3) return INVALID_COUPON_ISSUE_QUANTITY;

        throw new BaseException(400, "존재하지 않는 코드입니다.");
    }

    public static void checkRequestResult(CouponIssueRequestCode code) {
        if (code == INVALID_COUPON_ISSUE_QUANTITY) {
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_QUANTITY, "발급 가능한 수량을 초과했습니다.");
        }
        if (code == DUPLICATED_COUPON_ISSUE) {
            throw new ConflictException("이미 발급 요청된 쿠폰입니다.");
        }
    }
}
