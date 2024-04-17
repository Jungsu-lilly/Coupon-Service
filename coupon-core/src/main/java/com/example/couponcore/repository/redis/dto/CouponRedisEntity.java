package com.example.couponcore.repository.redis.dto;

import com.example.couponcore.exception.common.BaseException;
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
                        coupon.getIssueStartDate(),
                        coupon.getIssueEndDate()
                );
        }

        private boolean availableIssueDate() {
                LocalDateTime now = LocalDateTime.now();
                return issueStartDate.isBefore(now) && issueEndDate.isAfter(now);
        }

        public void checkIssuableCoupon() {
                if (!availableIssueDate()) {
                        throw new BaseException(400, "발급 가능한 일자가 아닙니다.");
                }
        }
}
