package com.example.couponcore.service;

import com.example.couponcore.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisCouponIssueService {

    private final RedisRepository redisRepository;

    public void issue(long couponId, long userId) {
        // 1. sorted set에 유저의 요청을 적재
        String key = "issue.sorted_set.couponid=%s".formatted(couponId);
        redisRepository.zAdd(key, String.valueOf(userId), System.currentTimeMillis());

        // 2. 유저의 요청 순서를 조회

        // 3. 조회 결과를 선착순 조건과 비교

        // 4. 쿠폰 발급 queue에 적재
    }
}
