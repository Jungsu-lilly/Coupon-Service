package com.example.couponcore.service;

import com.example.couponcore.exception.common.BaseException;
import com.example.couponcore.exception.common.ConflictException;
import com.example.couponcore.model.Coupon;
import com.example.couponcore.repository.redis.RedisRepository;
import com.example.couponcore.repository.redis.dto.CouponIssueRequest;
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
    private ObjectMapper objectMapper = new ObjectMapper();

    public void issue(long couponId, long userId) {
        Coupon coupon = couponIssueService.findCoupon(couponId);

        if (!coupon.verifyIssueDate()) {
            throw new BaseException(400, "발급 가능한 일자가 아닙니다. couponId : %s 날짜를 다시 확인해주세요.".formatted(couponId));
        }
        if (!redisCouponIssueService.availableTotalIssueQuantity(coupon.getTotalQuantity(), couponId)) {
            throw new BaseException(400, "쿠폰 발급 가능한 수량이 초과되었습니다. couponId : %s, userId : %s".formatted(couponId, userId));
        }
        if (!redisCouponIssueService.availableUserIssueQuantity(couponId, userId)) {
            throw new ConflictException("이미 쿠폰이 발급되었습니다. 또다시 발급할 수 없습니다.");
        }

        issueRequest(couponId, userId);
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
