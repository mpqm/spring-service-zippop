package com.fiiiiive.zippop.global.redis;

import org.junit.jupiter.api.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisQueueExpiryTest {
    private LettuceConnectionFactory connection;
    private RedisTemplate<String, Object> redis;
    private RedisQueueService service;
    private String working;
    private String waiting;

    @BeforeEach void connect() throws Exception {
        Assumptions.assumeTrue("true".equals(System.getenv("ZIPPOP_REDIS_INTEGRATION")));
        var config = new RedisStandaloneConfiguration("localhost", 6379);
        config.setPassword(System.getenv("ZIPPOP_REDIS_PASSWORD"));
        connection = new LettuceConnectionFactory(config);
        connection.afterPropertiesSet();
        connection.start();
        redis = new RedisTemplate<>();
        redis.setConnectionFactory(connection);
        redis.setKeySerializer(new StringRedisSerializer());
        redis.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        redis.afterPropertiesSet();
        var redisson = mock(RedissonClient.class);
        var lock = mock(RLock.class);
        when(redisson.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(5, 3, TimeUnit.SECONDS)).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);
        service = new RedisQueueService(redis, redisson);
        working = "test:fix:" + UUID.randomUUID();
        waiting = working + ":waiting";
        service.createQueue(working, 1);
        service.createQueue(waiting, 1);
    }

    @AfterEach void clean() {
        if (service != null) service.deleteQueue(working, waiting);
        if (connection != null) connection.destroy();
    }

    @Test void firstRealMemberGetsATtlAndRecreatedSetKeepsItsDeadline() {
        assertThat(redis.hasKey(working)).isFalse();
        assertThat(service.existQueue(working)).isTrue();
        service.enrollQueue(working, "one@example.test", System.currentTimeMillis());
        assertThat(redis.getExpire(working, TimeUnit.SECONDS)).isBetween(1L, 60L);
        service.remove(working, "one@example.test");
        assertThat(redis.hasKey(working)).isFalse();
        service.enrollQueue(working, "two@example.test", System.currentTimeMillis());
        assertThat(redis.getExpire(working, TimeUnit.SECONDS)).isBetween(1L, 60L);
    }

    @Test void promotionStartsANewTenMinuteLeaseInsteadOfUsingWaitingTime() {
        service.enrollQueue(waiting, "waiting@example.test", System.currentTimeMillis() - 900000);
        assertThat(service.firstWaitingUserToWorking(working, waiting, 1)).isEqualTo("waiting@example.test");
        assertThat(service.hasWorkingAccess(working, "waiting@example.test")).isTrue();
        assertThat(redis.getExpire(working, TimeUnit.SECONDS)).isBetween(1L, 60L);
        assertThat(service.getOrder(waiting, "waiting@example.test")).isNull();
    }

    @Test void expiredMemberIsDeniedAndRemovedBeforeCapacityIsChecked() {
        service.enrollQueue(working, "old@example.test", System.currentTimeMillis() - 600001);
        assertThat(service.hasWorkingAccess(working, "old@example.test")).isFalse();
        assertThat(service.enrollQueueWithLock(working, "new@example.test", System.currentTimeMillis(), 1)).isTrue();
        assertThat(service.getOrder(working, "old@example.test")).isNull();
    }

    @Test void existingWaiterIsPromotedBeforeANewArrival() {
        service.enrollQueue(waiting, "first@example.test", System.currentTimeMillis() - 1000);

        boolean newcomerAdmitted = service.enrollFairQueueWithLock(
                working, waiting, "new@example.test", System.currentTimeMillis(), 1);

        assertThat(newcomerAdmitted).isFalse();
        assertThat(service.getOrder(working, "first@example.test")).isZero();
        assertThat(service.getOrder(waiting, "new@example.test")).isZero();
    }
}
