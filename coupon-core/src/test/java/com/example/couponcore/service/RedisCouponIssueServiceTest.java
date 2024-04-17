package com.example.couponcore.service;

import com.example.couponcore.config.TestConfig;
import com.example.couponcore.repository.redis.RedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.stream.IntStream;

import static com.example.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static org.junit.jupiter.api.Assertions.*;

class RedisCouponIssueServiceTest extends TestConfig {

    @Autowired
    RedisCouponIssueService couponIssueService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    RedisRepository redisRepository;

    @BeforeEach
    void clear() {
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }

    @Test
    @DisplayName("쿠폰 수량 검증 - 발급 가능 수량이 있으0면 true 반환")
    void availableTotalIssueQuantity_1() {
        // given
        int totalIssueQuantity = 10;
        long couponId = 1L;

        // when
        boolean result = couponIssueService.availableTotalIssueQuantity(totalIssueQuantity, couponId);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("쿠폰 수량 검증 - 수량이 모두 소진되면 false 반환")
    void availableTotalIssueQuantity_2() {
        // given
        int totalIssueQuantity = 10;
        long couponId = 1L;
        IntStream.range(0, totalIssueQuantity).forEach(userId -> {
            redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));
        });

        // when
        Long aLong = redisRepository.sCard(getIssueRequestKey(couponId));
        assertEquals(aLong, 10);
        boolean result = couponIssueService.availableTotalIssueQuantity(totalIssueQuantity, couponId);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("쿠폰 중복 발급 검증 - 발급된 내역에 유저가 존재하지 않으면 true 반환")
    void availableUserIssueQuantity_1() {
        long couponId = 1;
        long userId = 1;

        boolean result = couponIssueService.availableUserIssueQuantity(couponId, userId);
        assertTrue(result);
    }

    @Test
    @DisplayName("쿠폰 중복 발급 검증 - 발급된 내역에 유저가 존재하면 false 반환")
    void availableUserIssueQuantity_2() {
        long couponId = 1;
        long userId = 1;
        redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));

        boolean result = couponIssueService.availableUserIssueQuantity(couponId, userId);

        assertFalse(result);
    }
}