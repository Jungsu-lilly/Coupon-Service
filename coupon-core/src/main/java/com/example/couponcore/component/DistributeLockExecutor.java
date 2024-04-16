package com.example.couponcore.component;

import com.example.couponcore.exception.common.BaseException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class DistributeLockExecutor {

    private final RedissonClient redissonClient;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public void execute(String lockName, long waitMilliSecond, long releaseMilliSecond, Runnable logic) {
        RLock lock = redissonClient.getLock(lockName);
        try {
            boolean result = lock.tryLock(waitMilliSecond, releaseMilliSecond, TimeUnit.MILLISECONDS);
            if (!result) {
                throw new BaseException(400, "["+lockName+"] lock 획득 실패");
            }
            logic.run();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
