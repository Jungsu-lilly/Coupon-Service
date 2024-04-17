package com.example.couponapi.service;

import com.example.couponapi.dto.CouponIssueRequestDto;
import com.example.couponcore.component.DistributeLockExecutor;
import com.example.couponcore.service.CouponIssueService;
import com.example.couponcore.service.RedisCouponIssueService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;
    private final RedisCouponIssueService redisCouponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public void issueCouponV1(CouponIssueRequestDto requestDto) {
        distributeLockExecutor.execute("lock_" + requestDto.couponId(), 10000, 10000, () -> {
            couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        });
        log.info("쿠폰 발급 완료. couponId: %s, userId: %s".formatted(requestDto.couponId(), requestDto.userId()));
    }

    public void asyncIssueRequest(CouponIssueRequestDto requestDto) {
        redisCouponIssueService.issue(requestDto.couponId(), requestDto.userId());
        log.info("쿠폰 발급 완료. couponId: %s, userId: %s".formatted(requestDto.couponId(), requestDto.userId()));
    }
}
