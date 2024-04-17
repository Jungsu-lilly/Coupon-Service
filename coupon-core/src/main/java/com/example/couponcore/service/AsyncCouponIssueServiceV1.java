package com.example.couponcore.service;

import com.example.couponcore.component.DistributeLockExecutor;
import com.example.couponcore.exception.common.BaseException;
import com.example.couponcore.exception.common.ConflictException;
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
    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final CouponCacheService couponCacheService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void issue(long couponId, long userId) {
        CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);
        coupon.checkIssuableCoupon();

        distributeLockExecutor.execute("lock_%s".formatted(couponId), 2000, 2000, () -> {
            if (!redisCouponIssueService.availableTotalIssueQuantity(coupon.totalQuantity(), couponId)) {
                throw new BaseException(400, "쿠폰 발급 가능한 수량이 초과되었습니다. couponId : %s, userId : %s".formatted(couponId, userId));
            }
            if (!redisCouponIssueService.availableUserIssueQuantity(couponId, userId)) {
                throw new ConflictException("이미 쿠폰이 발급되었습니다. 또다시 발급할 수 없습니다.");
            }
            issueRequest(couponId, userId);
        });
    }

    private void issueRequest(long couponId, long userId) {
        CouponIssueRequest issueRequest = new CouponIssueRequest(couponId, userId);
        try {
            String value = objectMapper.writeValueAsString(issueRequest);
            redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(userId));
            redisRepository.rPush(getQueueKey(), value);
        } catch (JsonProcessingException e) {
            throw new BaseException(400, "쿠폰 발급 실패");
        }
    }
}
