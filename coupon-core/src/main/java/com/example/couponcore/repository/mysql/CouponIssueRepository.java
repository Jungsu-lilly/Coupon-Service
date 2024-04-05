package com.example.couponcore.repository.mysql;

import com.example.couponcore.model.CouponIssuance;
import com.querydsl.jpa.JPQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.example.couponcore.model.QCouponIssuance.couponIssuance;

@RequiredArgsConstructor
@Repository
public class CouponIssueRepository {

    private final JPQLQueryFactory queryFactory;

    public CouponIssuance findFirstCouponIssuance(long couponId, long userId) {
        return queryFactory.selectFrom(couponIssuance)
                .where(couponIssuance.couponId.eq(couponId))
                .where(couponIssuance.userId.eq(userId))
                .fetchFirst();
    }
}
