package com.example.couponconsumer.component;

import com.example.couponcore.repository.redis.RedisRepository;
import com.example.couponcore.service.CouponIssueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Import(CouponIssueListener.class)
class CouponIssueListenerTest extends TestConfig {

    @Autowired
    CouponIssueListener listener;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisRepository redisRepository;

    @MockBean
    CouponIssueService couponIssueService;

    @BeforeEach
    void clear() {
        Collection<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);
    }

    @Test
    @DisplayName("쿠폰 발급 큐에 처리 대상이 없다면 발급을 안함")
    void issue_1() throws JsonProcessingException {
        // when
        listener.issue();

        // then
        verify(couponIssueService, never()).issue(anyLong(), anyLong());
    }

    @Test
    @DisplayName("쿠폰 발급 큐에 처리 대상이 있다면 발급한다.")
    void issue_2() throws JsonProcessingException {
        // given
        long couponId = 1;
        long userId = 1;
        int totalQuantity = Integer.MAX_VALUE;
        redisRepository.issueRequest(couponId, userId, totalQuantity);  // 데이터가 큐에 적재됨

        // when
        listener.issue();

        // then
        verify(couponIssueService, times(1)).issue(anyLong(), anyLong());
    }

    @Test
    @DisplayName("쿠폰 발급 요청 순서에 대한 검증")
    void issue_3() throws JsonProcessingException {
        // given
        long couponId = 1;
        long userId_1 = 1;
        long userId_2 = 2;
        long userId_3 = 3;
        int totalQuantity = Integer.MAX_VALUE;
        redisRepository.issueRequest(couponId, userId_1, totalQuantity);  // 데이터가 큐에 적재됨
        redisRepository.issueRequest(couponId, userId_2, totalQuantity);
        redisRepository.issueRequest(couponId, userId_3, totalQuantity);

        // when
        listener.issue();

        // then
        InOrder inOrder = Mockito.inOrder(couponIssueService);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId_1);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId_2);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId_3);
    }
}