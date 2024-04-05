package com.example.couponcore.model;

import com.example.couponcore.exception.CouponIssueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.example.couponcore.exception.ErrorCode.INVALID_COUPON_DATE;
import static com.example.couponcore.exception.ErrorCode.INVALID_COUPON_QUANTITY;
import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    @Test
    @DisplayName("발급수량이 남아있다면 true를 반환한다.")
    void verifyIssueQuantity_1() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(97)
                .build();
        // when
        boolean result = coupon.verifyIssueQuantity();

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("발급수량이 남아 있지 않다면 false를 반환한다.")
    void verifyIssueQuantity_2() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .build();
        // when
        boolean result = coupon.verifyIssueQuantity();

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("최대 발급 수량이 (null) 이라면 true 반환한다.")
    void verifyIssueQuantity_3() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(null)
                .issuedQuantity(99)
                .build();
        // when
        boolean result = coupon.verifyIssueQuantity();

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("발급이 시작되지 않았다면 false를 반환한다.")
    void verifyIssueDate_1() {
        // given
        Coupon coupon = Coupon.builder()
                .issueStartDate(LocalDateTime.now().plusDays(1))
                .issueEndDate(LocalDateTime.now().plusDays(2))
                .build();
        // when
        boolean result = coupon.verifyIssueDate();

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("발급 기간에 해당되면 true를 반환")
    void verifyIssueDate_2() {
        // given
        Coupon coupon = Coupon.builder()
                .issueStartDate(LocalDateTime.now().minusDays(1))
                .issueEndDate(LocalDateTime.now().plusDays(1))
                .build();
        // when
        boolean result = coupon.verifyIssueDate();

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("발급 기간이 종료되면 false를 반환")
    void verifyIssueDate_3() {
        // given
        Coupon coupon = Coupon.builder()
                .issueStartDate(LocalDateTime.now().minusDays(2))
                .issueEndDate(LocalDateTime.now().minusDays(1))
                .build();
        // when
        boolean result = coupon.verifyIssueDate();

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("발급 성공 케이스")
    void issue_1() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .issueStartDate(LocalDateTime.now().minusDays(1))
                .issueEndDate(LocalDateTime.now().plusDays(1))
                .build();
        // when
        coupon.issue();

        // then
        assertEquals(coupon.getIssuedQuantity(), 100);
    }

    @Test
    @DisplayName("발급 수량 초과 시, 예외를 반환")
    void issue_2() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .issueStartDate(LocalDateTime.now().minusDays(1))
                .issueEndDate(LocalDateTime.now().plusDays(1))
                .build();

        // when & then
        CouponIssueException exception = assertThrows(CouponIssueException.class, coupon::issue);
        assertEquals(exception.getErrorCode(), INVALID_COUPON_QUANTITY);
    }

    @Test
    @DisplayName("발급 기간이 아닌 경우, 예외를 반환")
    void issue_3() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(97)
                .issueStartDate(LocalDateTime.now().minusDays(3))
                .issueEndDate(LocalDateTime.now().minusDays(2))
                .build();

        // when & then
        CouponIssueException exception = assertThrows(CouponIssueException.class, coupon::issue);
        assertEquals(exception.getErrorCode(), INVALID_COUPON_DATE);
    }
}