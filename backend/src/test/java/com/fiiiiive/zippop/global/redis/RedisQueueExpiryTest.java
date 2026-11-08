package com.fiiiiive.zippop.global.redis;

import com.fiiiiive.zippop.reserve.infrastructure.redis.ReserveRedisService;
import com.fiiiiive.zippop.reserve.model.enums.EnrollResult;
import org.junit.jupiter.api.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisQueueExpiryTest {
    private LettuceConnectionFactory connection;
    private RedisTemplate<String, Object> redis;
    private ReserveRedisService service;
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
        service = new ReserveRedisService(redis, redisson);
        working = "test:fix:" + UUID.randomUUID();
        waiting = working + ":waiting";
        service.createQueue(working, LocalDateTime.now().plusMinutes(1));
        service.createQueue(waiting, LocalDateTime.now().plusMinutes(1));
    }

    @AfterEach void clean() {
        if (service != null) service.deleteQueue(working, waiting);
        if (connection != null) connection.destroy();
    }

    @Test void firstRealMemberGetsATtlAndRecreatedSetKeepsItsDeadline() {
        assertThat(redis.hasKey(working)).isFalse();
        assertThat(service.existQueue(working)).isTrue();
        assertThat(service.enrollWithLock(working, waiting, "one@example.test",
                System.currentTimeMillis(), 1)).isEqualTo(EnrollResult.WORKING);
        assertThat(redis.getExpire(working, TimeUnit.SECONDS)).isBetween(1L, 60L);
        service.remove(working, "one@example.test");
        assertThat(redis.hasKey(working)).isFalse();
        assertThat(service.enrollWithLock(working, waiting, "two@example.test",
                System.currentTimeMillis(), 1)).isEqualTo(EnrollResult.WORKING);
        assertThat(redis.getExpire(working, TimeUnit.SECONDS)).isBetween(1L, 60L);
    }

    @Test void promotionStartsANewTenMinuteLeaseInsteadOfUsingWaitingTime() {
        seedMember(waiting, "waiting@example.test", System.currentTimeMillis() - 900000);
        assertThat(service.promoteFirstMember(working, waiting, 1)).isEqualTo("waiting@example.test");
        assertThat(service.hasWorkingAccess(working, "waiting@example.test")).isTrue();
        assertThat(redis.getExpire(working, TimeUnit.SECONDS)).isBetween(1L, 60L);
        assertThat(service.getOrder(waiting, "waiting@example.test")).isNull();
    }

    @Test void expiredMemberIsDeniedAndRemovedBeforeCapacityIsChecked() {
        seedMember(working, "old@example.test", System.currentTimeMillis() - 600001);
        assertThat(service.hasWorkingAccess(working, "old@example.test")).isFalse();
        assertThat(service.enrollWithLock(working, waiting, "new@example.test",
                System.currentTimeMillis(), 1)).isEqualTo(EnrollResult.WORKING);
        assertThat(service.getOrder(working, "old@example.test")).isNull();
    }

    @Test void existingWaiterIsPromotedBeforeANewArrival() {
        seedMember(waiting, "first@example.test", System.currentTimeMillis() - 1000);

        EnrollResult newcomerResult = service.enrollWithLock(
                working, waiting, "new@example.test", System.currentTimeMillis(), 1);

        assertThat(newcomerResult).isEqualTo(EnrollResult.WAITING);
        assertThat(service.getOrder(working, "first@example.test")).isZero();
        assertThat(service.getOrder(waiting, "new@example.test")).isZero();
    }

    @Test void existingWaiterTakesAnAvailableWorkingSlot() {
        seedMember(waiting, "first@example.test", System.currentTimeMillis() - 1000);

        assertThat(service.enrollWithLock(working, waiting, "first@example.test",
                System.currentTimeMillis(), 1)).isEqualTo(EnrollResult.WORKING);
        assertThat(service.getOrder(working, "first@example.test")).isZero();
        assertThat(service.getOrder(waiting, "first@example.test")).isNull();
    }

    private void seedMember(String key, String email, long score) {
        redis.opsForZSet().add(key, email, score);
        redis.expire(key, 1, TimeUnit.MINUTES);
    }
}
