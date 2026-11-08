package com.fiiiiive.zippop.reserve.infrastructure.redis;

import com.fiiiiive.zippop.global.base.ServerErrorCode;
import com.fiiiiive.zippop.global.base.ServerException;
import com.fiiiiive.zippop.reserve.model.enums.EnrollResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class ReserveRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;
    private static final long ACCESS_MILLIS = TimeUnit.MINUTES.toMillis(10);
    private static final DefaultRedisScript<String> PROMOTE = new DefaultRedisScript<>("""
            local deadline = redis.call('GET', KEYS[3])
            if not deadline then return nil end
            deadline = tonumber(cjson.decode(deadline))
            if deadline <= tonumber(ARGV[1]) then return nil end
            redis.call('ZREMRANGEBYSCORE', KEYS[1], '-inf', tonumber(ARGV[1]) - 600000)
            if redis.call('ZCARD', KEYS[1]) >= tonumber(ARGV[2]) then return nil end
            local member = redis.call('ZRANGE', KEYS[2], 0, 0)[1]
            if not member then return nil end
            redis.call('ZREM', KEYS[2], member)
            redis.call('ZADD', KEYS[1], ARGV[1], member)
            redis.call('PEXPIREAT', KEYS[1], deadline)
            return member
            """, String.class);
    private static final DefaultRedisScript<Long> FAIR_ENROLL = new DefaultRedisScript<>("""
            local workingDeadline = redis.call('GET', KEYS[3])
            local waitingDeadline = redis.call('GET', KEYS[4])
            if not workingDeadline or not waitingDeadline then return -1 end
            workingDeadline = tonumber(cjson.decode(workingDeadline))
            waitingDeadline = tonumber(cjson.decode(waitingDeadline))
            local now = tonumber(ARGV[2])
            local maxSize = tonumber(ARGV[3])
            if workingDeadline <= now or waitingDeadline <= now then return -1 end
            redis.call('ZREMRANGEBYSCORE', KEYS[1], '-inf', now - 600000)
            if redis.call('ZSCORE', KEYS[1], ARGV[1]) then return 1 end
            while redis.call('ZCARD', KEYS[1]) < maxSize do
                local waitingMember = redis.call('ZRANGE', KEYS[2], 0, 0)[1]
                if not waitingMember then break end
                redis.call('ZREM', KEYS[2], waitingMember)
                redis.call('ZADD', KEYS[1], now, waitingMember)
                redis.call('PEXPIREAT', KEYS[1], workingDeadline)
            end
            if redis.call('ZSCORE', KEYS[1], ARGV[1]) then
                redis.call('PEXPIREAT', KEYS[1], workingDeadline)
                return 1
            end
            if redis.call('ZSCORE', KEYS[2], ARGV[1]) then return 0 end
            if redis.call('ZCARD', KEYS[2]) == 0 and redis.call('ZCARD', KEYS[1]) < maxSize then
                redis.call('ZADD', KEYS[1], now, ARGV[1])
                redis.call('PEXPIREAT', KEYS[1], workingDeadline)
                return 1
            end
            redis.call('ZADD', KEYS[2], now, ARGV[1])
            redis.call('PEXPIREAT', KEYS[2], waitingDeadline)
            return 0
            """, Long.class);

    // 큐 생성 초기화 및 만료 시간 설정
    public void createQueue(String key, LocalDateTime endTime) {
        try {
            long deadline = endTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) throw new IllegalArgumentException("Queue lifetime must be positive");
            redisTemplate.opsForValue().set(key + ":deadline", Long.toString(deadline),
                    remaining, TimeUnit.MILLISECONDS);
            redisTemplate.expireAt(key, new Date(deadline));
        } catch (Exception e) {
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_CREATE_ERROR, e);
        }
    }

    // 락) 기존 멤버 확인, 만료자 정리, 대기자 우선 승격, 신규 등록
    public EnrollResult enrollWithLock(String workingKey, String waitingKey, String value, long timestamp, int maxSize) {
        RLock lock = redissonClient.getLock("lock:" + workingKey);

        try {
            if (!lock.tryLock(5, 3, TimeUnit.SECONDS)) throw new IllegalStateException("예약 큐 락을 획득하지 못했습니다.");

            Long result = redisTemplate.execute(
                    FAIR_ENROLL,
                    List.of(workingKey, waitingKey, workingKey + ":deadline", waitingKey + ":deadline"),
                    value, timestamp, maxSize
            );

            if (result == null || result < 0) throw new IllegalStateException("예약 큐가 만료되었습니다.");

            return result == 1 ? EnrollResult.WORKING : EnrollResult.WAITING;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_ENROLL_ERROR, e);
        } catch (Exception e) {
            if (e instanceof ServerException serverException) throw serverException;
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_ENROLL_ERROR, e);
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    //  큐 만료로 사용자 제거
    public void removeExpireMembers(String key) {
        redisTemplate.opsForZSet().removeRangeByScore(key, Double.NEGATIVE_INFINITY, System.currentTimeMillis() - ACCESS_MILLIS);
    }

    // 대기 큐에서 첫 번째 사용자 → 작업 큐로 이동
    public String promoteFirstMember(String key1, String key2, Integer fixedSize) {
        if (key1 == null || key2 == null || fixedSize == null) {
            return null;
        }

        RLock lock = redissonClient.getLock("lock:" + key1);
        try {
            boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!isLocked) return null;
            return redisTemplate.execute(PROMOTE, List.of(key1, key2, key1 + ":deadline"), System.currentTimeMillis(), fixedSize);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_FIRST_USER_ERROR, exception);
        } catch (Exception exception) {
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_FIRST_USER_ERROR, exception);
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    // 사용자 예약 접속권 끝나는 시각 반환
    public Long getMemberExpireAt(String key, String email) {
        Double admitted = redisTemplate.opsForZSet().score(key, email);
        return admitted == null ? null : admitted.longValue() + ACCESS_MILLIS;
    }

    public boolean hasWorkingAccess(String key, String email) {
        Double admitted = redisTemplate.opsForZSet().score(key, email);
        return admitted != null && admitted > System.currentTimeMillis() - ACCESS_MILLIS;
    }

    // 큐 존재 여부 확인
    public boolean existQueue(String key) {
        try {
            return redisTemplate.hasKey(key + ":deadline");
        } catch (Exception exception) {
            throw new ServerException(ServerErrorCode.REDIS_READ_ERROR, exception);
        }
    }

    // 큐 삭제
    public void deleteQueue(String workingQueueUUID, String waitingQueueUUID) {
        try {
            redisTemplate.delete(workingQueueUUID);
            redisTemplate.delete(waitingQueueUUID);
            redisTemplate.delete(workingQueueUUID + ":deadline");
            redisTemplate.delete(waitingQueueUUID + ":deadline");
        } catch (Exception exception) {
            throw new ServerException(ServerErrorCode.REDIS_DELETE_ERROR, exception);
        }
    }

    // SortedSet 값 삭제
    public void remove(String key, String value) {
        try {
            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
            zSetOperations.remove(key, value);
        } catch (Exception exception) {
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_REMOVE_ERROR, exception);
        }
    }

    // SortedSet 전체 값(로그) 조회 및 사이즈
    public String getSize(String key) {
        try {
            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
            Set<ZSetOperations.TypedTuple<Object>> values = zSetOperations.rangeWithScores(key, 0, -1);
            Objects.requireNonNull(values).forEach(value -> log.info("Value: " + value.getValue() + ", Score: " + value.getScore()) );
            String total = String.valueOf(zSetOperations.zCard(key));
            log.info(total);
            return total;
        } catch (Exception exception) {
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_SIZE_ERROR, exception);
        }
    }

    // SortedSet 내 값의 순위 조회
    public Long getOrder(String key, String value) {
        try {
            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
            return zSetOperations.rank(key, value);
        } catch (Exception exception) {
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_ORDER_ERROR, exception);
        }
    }

}
