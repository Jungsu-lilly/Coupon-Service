package com.example.couponconsumer.component;

import com.example.couponcore.repository.redis.RedisRepository;
import com.example.couponcore.repository.redis.dto.CouponIssueRequest;
import com.example.couponcore.service.CouponIssueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.example.couponcore.util.CouponRedisUtils.getQueueKey;

@RequiredArgsConstructor
@EnableScheduling
@Component
@Slf4j
public class CouponIssueListener {

    private final RedisRepository redisRepository;
    private final CouponIssueService couponIssueService;
    private final ObjectMapper objectMapper;
    private final String issueRequestQueueKey = getQueueKey();


    /**
     * 주기적으로 스케쥴링 */
    @Scheduled(fixedDelay = 1000L)
    public void issue() throws JsonProcessingException {
        log.info("listen...");

        while(existCouponIssueTarget()) {
            CouponIssueRequest target = getIssueTarget();
            log.info("발급 시작 target: %s".formatted(target));
            couponIssueService.issue(target.couponId(), target.userId());
            log.info("발급 완료 target: %s".formatted(target));
            removeIssuedTarget();
        }
    }

    private void removeIssuedTarget() {
        redisRepository.lPop(issueRequestQueueKey);
    }

    private boolean existCouponIssueTarget() {  // 큐에 대상이 있는지 확인!
        if (redisRepository.lSize(issueRequestQueueKey) > 0) {
            return true;
        } else return false;
    }

    /**
     * 큐에서 가장 앞에 있는 인덱스를 읽어서, 가져온다. */
    private CouponIssueRequest getIssueTarget() throws JsonProcessingException {
        return objectMapper.readValue(
                redisRepository.lIndex(issueRequestQueueKey, 0), CouponIssueRequest.class
        );
    }
}
