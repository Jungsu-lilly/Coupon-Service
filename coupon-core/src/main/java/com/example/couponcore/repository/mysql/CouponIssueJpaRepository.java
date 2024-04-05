package com.example.couponcore.repository.mysql;

import com.example.couponcore.model.CouponIssuance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssuance, Long> {
}
