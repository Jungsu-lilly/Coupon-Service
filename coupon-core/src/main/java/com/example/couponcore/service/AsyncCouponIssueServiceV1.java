package com.example.couponcore.service;

import com.example.couponcore.exception.common.BaseException;
import com.example.couponcore.repository.redis.RedisRepository;
import com.example.couponcore.repository.redis.dto.CouponIssueRequest;
import com.example.couponcore.repository.redis.dto.CouponRedisEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static com.example.couponcore.util.CouponRedisUtils.getQueueKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV1 {

    private final RedisRepository redisRepository;
    private final RedisCouponIssueService redisCouponIssueService;
    private final CouponCacheService couponCacheService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void issue(long couponId, long userId) {
        CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);

        redisCouponIssueService.verifyCouponIssuance(coupon, userId);
        issueCoupon(couponId, userId);
    }

    private void issueCoupon(long couponId, long userId) {
        CouponIssueRequest issueRequest = new CouponIssueRequest(couponId, userId);
        try {
            String value = objectMapper.writeValueAsString(issueRequest);
            redisRepository.rPush(getQueueKey(), value);
            redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(userId));
        } catch (JsonProcessingException e) {
            throw new BaseException(400, "쿠폰 발급 실패");
        }
    }
}
