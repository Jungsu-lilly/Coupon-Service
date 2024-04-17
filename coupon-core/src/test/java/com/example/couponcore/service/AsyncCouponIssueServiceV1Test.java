package com.example.couponcore.service;

import com.example.couponcore.config.TestConfig;
import com.example.couponcore.exception.common.BaseException;
import com.example.couponcore.exception.common.DataNotFoundException;
import com.example.couponcore.model.Coupon;
import com.example.couponcore.model.CouponType;
import com.example.couponcore.repository.mysql.CouponJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static com.example.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static org.junit.jupiter.api.Assertions.*;

class AsyncCouponIssueServiceV1Test extends TestConfig {

    @Autowired
    AsyncCouponIssueServiceV1 serviceV1;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    CouponJpaRepository couponJpaRepository;

    @BeforeEach
    void clear() {
        Set<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰정책이 존재하지 않는다먄 예외 반환")
    void issueCoupon1() {
        long couponId = 1;
        long userId = 1;

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () ->
                serviceV1.issue(couponId, userId));
        assertEquals(exception.getCode(), 404);
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 가능 수량이 존재하지 않는다면 예외 반환")
    void issueCoupon2() {
        // given
        long userId = 1000;

        Coupon coupon = Coupon.builder()
                .type(CouponType.FIRST_COME_FIRST_SERVED)
                .name("선착순 쿠폰 A")
                .totalQuantity(10)
                .issuedQuantity(0)
                .issueStartDate(LocalDateTime.now().minusDays(1))
                .issueEndDate(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        IntStream.range(0, 10).forEach(idx -> {
            redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(idx));
        });

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            serviceV1.issue(coupon.getId(), userId);
        });
        assertEquals(exception.getCode(), 400);
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 기간이 아닌 경우 예외 반환")
    void issueCoupon3() {
        // given
        long userId = 1;

        Coupon coupon = Coupon.builder()
                .type(CouponType.FIRST_COME_FIRST_SERVED)
                .name("선착순 쿠폰 A")
                .totalQuantity(10)
                .issuedQuantity(0)
                .issueStartDate(LocalDateTime.now().minusDays(3))
                .issueEndDate(LocalDateTime.now().minusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(userId));

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            serviceV1.issue(coupon.getId(), userId);
        });
        assertEquals(exception.getCode(), 400);
    }

}