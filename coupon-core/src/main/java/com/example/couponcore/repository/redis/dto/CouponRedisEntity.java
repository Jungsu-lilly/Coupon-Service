package com.example.couponcore.repository.redis.dto;

import com.example.couponcore.exception.ErrorCode;
import com.example.couponcore.exception.custom.CouponIssueException;
import com.example.couponcore.model.Coupon;
import com.example.couponcore.model.CouponType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public record CouponRedisEntity(
        Long id,
        CouponType couponType,
        Integer totalQuantity,
        boolean issuanceQuantityAvailable,

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime issueStartDate,

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime issueEndDate
) {
        public CouponRedisEntity(Coupon coupon) {
                this(
                        coupon.getId(),
                        coupon.getType(),
                        coupon.getTotalQuantity(),
                        coupon.verifyIssueQuantity(),
                        coupon.getIssueStartDate(),
                        coupon.getIssueEndDate()
                );
        }

        private boolean checkIssuanceDate() {
                LocalDateTime now = LocalDateTime.now();
                return issueStartDate.isBefore(now) && issueEndDate.isAfter(now);
        }

        public void checkIfIssuable() {
                if (!issuanceQuantityAvailable) {
                        throw new CouponIssueException(ErrorCode.INVALID_COUPON_QUANTITY, "모든 발급 수량이 소진되었습니다. coupon_id : %s".formatted(id));
                }
                if (!checkIssuanceDate()) {
                        throw new CouponIssueException(ErrorCode.INVALID_COUPON_QUANTITY, "발급 가능한 일자가 아닙니다.");
                }
        }
}
