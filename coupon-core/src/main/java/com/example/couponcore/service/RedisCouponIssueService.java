package com.example.couponcore.service;

import com.example.couponcore.exception.ErrorCode;
import com.example.couponcore.exception.common.ConflictException;
import com.example.couponcore.exception.custom.CouponIssueException;
import com.example.couponcore.repository.redis.RedisRepository;
import com.example.couponcore.repository.redis.dto.CouponRedisEntity;
import com.example.couponcore.util.CouponRedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisCouponIssueService {

    private final RedisRepository redisRepository;

    public void verifyCouponIssuance(CouponRedisEntity coupon, long userId) {
        if (!hasSufficientQuantity(coupon.totalQuantity(), coupon.id())) {
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_QUANTITY, "발급 가능한 수량을 초과합니다. couponId : %s, userId : %s".formatted(coupon.id(), userId));
        }
        if (!isCouponAvailableForUser(coupon.id(), userId)) {
            throw new ConflictException("해당 유저에게 이미 발급된 쿠폰입니다.");
        }
    }

    public boolean hasSufficientQuantity(Integer totalQuantity, long couponId) {
        if (totalQuantity == null) {
            return true;
        }
        String key = CouponRedisUtils.getIssueRequestKey(couponId);
        return totalQuantity > redisRepository.sCard(key);
    }

    public boolean isCouponAvailableForUser(long couponId, long userId) {
        String key = CouponRedisUtils.getIssueRequestKey(couponId);
        return !redisRepository.sIsMember(key, String.valueOf(userId));
    }
}
