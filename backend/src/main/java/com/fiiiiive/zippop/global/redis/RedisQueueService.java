package com.fiiiiive.zippop.global.redis;

import com.fiiiiive.zippop.global.base.ServerErrorCode;
import com.fiiiiive.zippop.global.base.ServerException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Date;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Slf4j
@Service
@AllArgsConstructor
public class RedisQueueService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    private static final long ACCESS_MILLIS = TimeUnit.MINUTES.toMillis(10);
    private static final DefaultRedisScript<Long> ADD = new DefaultRedisScript<>("""
            local deadline = redis.call('GET', KEYS[2])
            if not deadline then return -1 end
            deadline = tonumber(cjson.decode(deadline))
            if deadline <= tonumber(ARGV[2]) then return -1 end
            if redis.call('ZSCORE', KEYS[1], ARGV[1]) then return 1 end
            if tonumber(ARGV[3]) > 0 and redis.call('ZCARD', KEYS[1]) >= tonumber(ARGV[3]) then return 0 end
            redis.call('ZADD', KEYS[1], ARGV[2], ARGV[1])
            redis.call('PEXPIREAT', KEYS[1], deadline)
            return 1
            """, Long.class);
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
            if redis.call('ZSCORE', KEYS[2], ARGV[1]) then return 0 end
            while redis.call('ZCARD', KEYS[1]) < maxSize do
                local waitingMember = redis.call('ZRANGE', KEYS[2], 0, 0)[1]
                if not waitingMember then break end
                redis.call('ZREM', KEYS[2], waitingMember)
                redis.call('ZADD', KEYS[1], now, waitingMember)
            end
            if redis.call('ZCARD', KEYS[2]) == 0 and redis.call('ZCARD', KEYS[1]) < maxSize then
                redis.call('ZADD', KEYS[1], now, ARGV[1])
                redis.call('PEXPIREAT', KEYS[1], workingDeadline)
                return 1
            end
            redis.call('ZADD', KEYS[2], now, ARGV[1])
            redis.call('PEXPIREAT', KEYS[2], waitingDeadline)
            return 0
            """, Long.class);

    public void expireWorkingMembers(String key) {
        redisTemplate.opsForZSet().removeRangeByScore(key, Double.NEGATIVE_INFINITY,
                System.currentTimeMillis() - ACCESS_MILLIS);
    }

    public boolean hasWorkingAccess(String key, String email) {
        Double admitted = redisTemplate.opsForZSet().score(key, email);
        return admitted != null && admitted > System.currentTimeMillis() - ACCESS_MILLIS;
    }

    public Long getWorkingAccessExpiresAt(String key, String email) {
        Double admitted = redisTemplate.opsForZSet().score(key, email);
        return admitted == null ? null : admitted.longValue() + ACCESS_MILLIS;
    }

    private boolean addMember(String key, String value, long timestamp, int limit) {
        Long result = redisTemplate.execute(ADD, List.of(key, key + ":deadline"), value, timestamp, limit);
        if (result == null || result < 0) throw new IllegalStateException("예약 큐가 만료되었습니다.");
        return result == 1;
    }

    // 큐 생성 초기화 및 만료 시간 설정
    public void createQueue(String key, long expirationTimeMinutes) {
        createQueueDeadline(key, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(expirationTimeMinutes));
    }

    public void createQueueUntil(String key, java.time.LocalDateTime endTime) {
        createQueueDeadline(key, endTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    private void createQueueDeadline(String key, long deadline) {
        try {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) throw new IllegalArgumentException("Queue lifetime must be positive");
            redisTemplate.opsForValue().set(key + ":deadline", Long.toString(deadline),
                    remaining, TimeUnit.MILLISECONDS);
            redisTemplate.expireAt(key, new Date(deadline));
        } catch (Exception e) {
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_CREATE_ERROR, e);
        }
    }

    // 큐 존재 여부 확인
    public boolean existQueue(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key + ":deadline"));
        } catch (Exception exception) {
            throw new ServerException(ServerErrorCode.REDIS_READ_ERROR, exception);
        }
    }

    // 큐 등록
    public void enrollQueue(String key, String value, long timestamp) {
        try {
            addMember(key, value, timestamp, 0);
        } catch (Exception exception) {
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_ENROLL_ERROR, exception);
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

    // SortedSet 값 삭제
    public void remove(String key, String value) {
        try {
            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
            zSetOperations.remove(key, value);
        } catch (Exception exception) {
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_REMOVE_ERROR, exception);
        }
    }

    // 예약 토큰 블랙리스트에 추가 (만료 시간: 10분)
    public void blacklistReserveToken(String token) {
        try {
            redisTemplate.opsForValue().set("blacklist:wtoken:" + token, "revoked", 10, TimeUnit.MINUTES);
            log.info("예약 토큰 블랙리스트 추가: {}", token);
        } catch (Exception e) {
            log.error("예약 토큰 블랙리스트 추가 중 오류: {}", e.getMessage());
            throw new ServerException(ServerErrorCode.REDIS_SAVE_ERROR, e);
        }
    }

    // 예약 토큰이 블랙리스트에 있는지 확인
    public boolean isReserveTokenBlacklisted(String token) {
        try {
            return redisTemplate.hasKey("blacklist:wtoken:" + token);
        } catch (Exception e) {
            log.error("예약 토큰 블랙리스트 확인 중 오류: {}", e.getMessage());
            throw new ServerException(ServerErrorCode.REDIS_READ_ERROR, e);
        }
    }


    // 대기 큐에서 첫 번째 사용자 → 작업 큐로 이동
    public String firstWaitingUserToWorking(String key1, String key2, Integer fixedSize) {
        if (key1 == null || key2 == null || fixedSize == null) {
            return null;
        }

        RLock lock = redissonClient.getLock("lock:" + key1);
        try {
            boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!isLocked) {
                return null;
            }

            return redisTemplate.execute(PROMOTE, List.of(key1, key2, key1 + ":deadline"),
                    System.currentTimeMillis(), fixedSize);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_FIRST_USER_ERROR, exception);
        } catch (Exception exception) {
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_FIRST_USER_ERROR, exception);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // Redisson 분산 락을 사용한 원자적 큐 등록 (정원 제한 포함) Pub/Sub 방식으로 스핀 락 문제 해결
    public boolean enrollQueueWithLock(String key, String value, long timestamp, int maxSize) {
        // 락 키 생성 (큐별로 독립적인 락)
        RLock lock = redissonClient.getLock("lock:" + key);
        
        try {
            // tryLock: 락 획득 시도 (락을 기다리는 최대 시간: 5초, 락을 자동으로 해제하는 시간(데드락방지): 3초)
            boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!isLocked) {
                return false;
            }
            
            log.info("🔒 락 획득 성공 - key: {}, value: {}", key, value);
            
            // 락을 획득한 상태에서 작업 수행
            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
            expireWorkingMembers(key);
            
            // 중복 체크 (이미 큐에 있는지 확인)
            Double existingScore = zSetOperations.score(key, value);
            if (existingScore != null) {
                return true;
            }
            
            // 큐 크기 확인
            Long currentSize = zSetOperations.zCard(key);
            if (currentSize == null) {
                currentSize = 0L;
            }
            
            log.info("📊 현재 큐 크기: {}/{}, value: {}", currentSize, maxSize, value);
            
            // 정원 확인 및 등록
            if (currentSize < maxSize) {
                // 정원 내 - 등록 성공
                if (addMember(key, value, timestamp, maxSize)) {
                    log.info("✅ 큐 등록 성공 - key: {}, value: {}, 현재 크기: {}/{}",  key, value, currentSize + 1, maxSize);
                    return true;
                } else {
                    log.warn("⚠️ 큐 등록 실패 - key: {}, value: {}", key, value);
                    return false;
                }
            } else {
                // 정원 초과 - 등록 실패
                log.info("❌ 큐 정원 초과 - key: {}, value: {}, 현재 크기: {}/{}", key, value, currentSize, maxSize);
                return false;
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("⚠️ 락 대기 중 인터럽트 발생 - key: {}, error: {}", key, e.getMessage());
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_ENROLL_ERROR, e);
        } catch (Exception e) {
            log.error("❌ 큐 등록 중 오류 발생 - key: {}, error: {}", key, e.getMessage(), e);
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_ENROLL_ERROR, e);
        } finally {
            // 락 해제 (반드시 finally에서 실행)
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("🔓 락 해제 완료 - key: {}", key);
            }
        }
    }

    public boolean enrollFairQueueWithLock(String workingKey, String waitingKey, String value,
                                           long timestamp, int maxSize) {
        RLock lock = redissonClient.getLock("lock:" + workingKey);
        try {
            if (!lock.tryLock(5, 3, TimeUnit.SECONDS)) {
                return false;
            }
            Long result = redisTemplate.execute(FAIR_ENROLL,
                    List.of(workingKey, waitingKey, workingKey + ":deadline", waitingKey + ":deadline"),
                    value, timestamp, maxSize);
            if (result == null || result < 0) {
                throw new IllegalStateException("예약 큐가 만료되었습니다.");
            }
            return result == 1;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_ENROLL_ERROR, exception);
        } catch (Exception exception) {
            throw new ServerException(ServerErrorCode.REDIS_QUEUE_ENROLL_ERROR, exception);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
