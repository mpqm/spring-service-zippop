package com.fiiiiive.zippop.reserve.service;

import com.fiiiiive.zippop.global.base.ServerErrorCode;
import com.fiiiiive.zippop.global.base.ServerException;
import com.fiiiiive.zippop.reserve.infrastructure.redis.ReserveRedisService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ReserveRedisServiceTest {

    @Test
    void lockTimeoutDoesNotReportWaitingEnrollment() throws InterruptedException {
        @SuppressWarnings("unchecked")
        RedisTemplate<String, Object> redis = mock(RedisTemplate.class);
        RedissonClient redisson = mock(RedissonClient.class);
        RLock lock = mock(RLock.class);
        when(redisson.getLock("lock:working")).thenReturn(lock);
        when(lock.tryLock(5, 3, TimeUnit.SECONDS)).thenReturn(false);

        ReserveRedisService service = new ReserveRedisService(redis, redisson);

        assertThatThrownBy(() -> service.enrollWithLock(
                "working", "waiting", "user@example.test", System.currentTimeMillis(), 1))
                .isInstanceOfSatisfying(ServerException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ServerErrorCode.REDIS_QUEUE_ENROLL_ERROR));
        verifyNoInteractions(redis);
        verify(lock, never()).unlock();
    }
}
