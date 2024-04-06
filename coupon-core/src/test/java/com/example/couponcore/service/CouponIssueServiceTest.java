package com.example.couponcore.service;

import com.example.couponcore.config.TestConfig;
import com.example.couponcore.exception.ErrorCode;
import com.example.couponcore.exception.common.ConflictException;
import com.example.couponcore.exception.custom.CouponIssueException;
import com.example.couponcore.model.Coupon;
import com.example.couponcore.model.CouponIssuance;
import com.example.couponcore.model.CouponType;
import com.example.couponcore.repository.mysql.CouponIssueJpaRepository;
import com.example.couponcore.repository.mysql.CouponIssueRepository;
import com.example.couponcore.repository.mysql.CouponJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponIssueServiceTest extends TestConfig {

    @Autowired
    CouponIssueService couponIssueService;

    @Autowired
    CouponIssueJpaRepository couponIssueJpaRepository;

    @Autowired
    CouponIssueRepository couponIssueRepository;

    @Autowired
    CouponJpaRepository couponJpaRepository;

    @BeforeEach
    void clean() {
        couponJpaRepository.deleteAllInBatch();
        couponIssueJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("쿠폰 발급 내역이 존재하면 예외를 반환")
    void saveCouponIssue_1() {
        // given
        CouponIssuance couponIssuance = CouponIssuance.builder()
                .couponId(1L)
                .userId(1L)
                .build();
        couponIssueJpaRepository.save(couponIssuance);
        // when
        ConflictException exception = assertThrows(ConflictException.class, () ->
                couponIssueService.saveCouponIssue(couponIssuance.getCouponId(), couponIssuance.getUserId())
        );

        //then
        assertEquals(exception.getCode(), 409);
    }

    @Test
    @DisplayName("쿠폰 발급 내역이 없다면 정상적으로 발급")
    void saveCouponIssue_2() {
        // given
        long couponId = 1L;
        long userId = 1L;
        // when
        CouponIssuance result = couponIssueService.saveCouponIssue(couponId, userId);

        // then
        assertTrue(couponIssueJpaRepository.findById(result.getId()).isPresent());
    }

    @Test
    @DisplayName("발급 수량, 기한, 중복 문제가 없는 경우 정상적으로 발급")
    void issueCoupon_1() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon.builder()
                .type(CouponType.FIRST_COME_FIRST_SERVED)
                .name("선착순 쿠폰 1")
                .totalQuantity(100)
                .issuedQuantity(10)
                .issueStartDate(LocalDateTime.now().minusDays(1))
                .issueEndDate(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // when
        couponIssueService.issue(coupon.getId(), userId);

        // then
        Coupon result = couponJpaRepository.findById(coupon.getId()).get();
        assertEquals(result.getIssuedQuantity(), 11);

        CouponIssuance firstCouponIssuance = couponIssueRepository.findFirstCouponIssuance(coupon.getId(), userId);
        assertNotNull(firstCouponIssuance);
    }

    @Test
    @DisplayName("모두 발급된 경우, 에러 반환")
    void issueCoupon_2() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon.builder()
                .type(CouponType.FIRST_COME_FIRST_SERVED)
                .name("선착순 쿠폰 1")
                .totalQuantity(100)
                .issuedQuantity(100)
                .issueStartDate(LocalDateTime.now().minusDays(1))
                .issueEndDate(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // when & then
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> {
            couponIssueService.issue(coupon.getId(), userId);
        });
        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COUPON_QUANTITY);
    }

    @Test
    @DisplayName("발급 기한이 유효하지 않은 경우, 예외ㅌ 반환")
    void issueCoupon_3() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon.builder()
                .type(CouponType.FIRST_COME_FIRST_SERVED)
                .name("선착순 쿠폰 1")
                .totalQuantity(100)
                .issuedQuantity(30)
                .issueStartDate(LocalDateTime.now().minusDays(2))
                .issueEndDate(LocalDateTime.now().minusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // when & then
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> {
            couponIssueService.issue(coupon.getId(), userId);
        });
        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COUPON_DATE);
    }

    @Test
    @DisplayName("중복해서 발급 불가")
    void issueCoupon_4() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon.builder()
                .type(CouponType.FIRST_COME_FIRST_SERVED)
                .name("선착순 쿠폰 1")
                .totalQuantity(100)
                .issuedQuantity(30)
                .issueStartDate(LocalDateTime.now().minusDays(1))
                .issueEndDate(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        CouponIssuance couponIssuance = CouponIssuance.builder()
                .couponId(coupon.getId())
                .userId(userId)
                .build();
        couponIssueJpaRepository.save(couponIssuance);

        // when & then
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            couponIssueService.issue(coupon.getId(), userId);
        });
        assertEquals(exception.getCode(), 409);
    }
}