package com.example.couponcore.service;

import com.example.couponcore.exception.common.ConflictException;
import com.example.couponcore.exception.common.DataNotFoundException;
import com.example.couponcore.model.Coupon;
import com.example.couponcore.model.CouponIssuance;
import com.example.couponcore.repository.mysql.CouponIssueJpaRepository;
import com.example.couponcore.repository.mysql.CouponIssueRepository;
import com.example.couponcore.repository.mysql.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CouponIssueService {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final CouponIssueRepository couponIssueRepository;

    @Transactional
    public void issue(long couponId, long userId) {
        Coupon coupon = findCoupon(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);
    }

    @Transactional(readOnly = true)
    public Coupon findCoupon(long couponId) {
        return couponJpaRepository.findById(couponId).orElseThrow(() -> {
            throw new DataNotFoundException("OMG Coupon Entity not found.");
        });
    }

    @Transactional
    public CouponIssuance saveCouponIssue(long couponId, long userId) {
        checkCouponIssuance(couponId, userId);
        CouponIssuance couponIssuance = CouponIssuance.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
        return couponIssueJpaRepository.save(couponIssuance);
    }

    private void checkCouponIssuance(long couponId, long userId) {
        CouponIssuance issuance = couponIssueRepository.findFirstCouponIssuance(couponId, userId);

        if (issuance != null) {
            throw new ConflictException("이미 발급된 쿠폰입니다. coupon_id: %s, user_id: %s".formatted(couponId, userId));
        }
    }
}
