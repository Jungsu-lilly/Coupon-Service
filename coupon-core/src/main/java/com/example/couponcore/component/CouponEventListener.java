package com.example.couponcore.component;

import com.example.couponcore.model.event.CouponIssueCompleteEvent;
import com.example.couponcore.service.CouponCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
@Slf4j
public class CouponEventListener {

    private final CouponCacheService couponCacheService;

    /**
     * issue complete 이벤트가 오면, 해당 트랜잭션이 커밋된 후 리스너 실행
     * Redis Cache, 로컬 캐시를 업데이트 해준다.
     * 그 다음 요청부터는 CouponRedisEntity.availableIssueQuantity = false 로 저장됨.
     * */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void issueComplete(CouponIssueCompleteEvent event) {
        log.info("issue complete. cache refresh start couponId: %s".formatted(event.couponId()));
        couponCacheService.putCouponCache(event.couponId());
        couponCacheService.putCouponLocalCache(event.couponId());
        log.info("issue complete. cache refresh end couponId: %s".formatted(event.couponId()));
    }
}
