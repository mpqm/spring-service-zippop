package com.fiiiiive.zippop.global.redis;

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

@Slf4j
@Service
@AllArgsConstructor
public class RedisQueueService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    // 큐 생성 초기화 및 만료 시간 설정
    public void createQueue(String key, long expirationTimeMinutes) {
        try {
            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
            zSetOperations.add(key, "start", System.currentTimeMillis());
            zSetOperations.remove(key, "start");
            redisTemplate.expire(key, expirationTimeMinutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 큐 존재 여부 확인
    public boolean existQueue(String key) {
        return redisTemplate.hasKey(key);
    }

    // 큐 등록
    public void enrollQueue(String key, String value, long timestamp) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(key, value, timestamp);
    }

    // 큐 삭제
    public void deleteQueue(String workingQueueUUID, String waitingQueueUUID) {
        redisTemplate.delete(workingQueueUUID);
        redisTemplate.delete(waitingQueueUUID);
    }

    // SortedSet 전체 값(로그) 조회 및 사이즈
    public String getSize(String key) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> values = zSetOperations.rangeWithScores(key, 0, -1);
        Objects.requireNonNull(values).forEach(value -> log.info("Value: " + value.getValue() + ", Score: " + value.getScore()) );
        String total = String.valueOf(zSetOperations.zCard(key));
        log.info(total);
        return total;
    }

    // SortedSet 내 값의 순위 조회
    public Long getOrder(String key, String value) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.rank(key, value);
    }

    // SortedSet 값 삭제
    public void remove(String key, String value) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.remove(key, value);
    }

    // 예약 토큰 블랙리스트에 추가 (만료 시간: 10분)
    public void blacklistReserveToken(String token) {
        try {
            redisTemplate.opsForValue().set("blacklist:wtoken:" + token, "revoked", 10, TimeUnit.MINUTES);
            log.info("예약 토큰 블랙리스트 추가: {}", token);
        } catch (Exception e) {
            log.error("예약 토큰 블랙리스트 추가 중 오류: {}", e.getMessage());
        }
    }

    // 예약 토큰이 블랙리스트에 있는지 확인
    public boolean isReserveTokenBlacklisted(String token) {
        try {
            return redisTemplate.hasKey("blacklist:wtoken:" + token);
        } catch (Exception e) {
            log.error("예약 토큰 블랙리스트 확인 중 오류: {}", e.getMessage());
            return false;
        }
    }


    // 대기 큐에서 첫 번째 사용자 → 작업 큐로 이동
    public String firstWaitingUserToWorking(String key1, String key2, Integer fixedSize) {
        if (key1 == null || key2 == null || fixedSize == null) {
            return null;
        }

        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> waitingList = zSetOperations.rangeWithScores(key2, 0, 0);

        if (waitingList == null || waitingList.isEmpty()) {
            return null;
        }
        Long currentSize = zSetOperations.zCard(key1);
        if (currentSize == null || currentSize >= fixedSize) {
            return null;
        }
        ZSetOperations.TypedTuple<Object> firstWaitingUser = waitingList.iterator().next();
        if (firstWaitingUser == null || firstWaitingUser.getValue() == null || firstWaitingUser.getScore() == null) {
            return null;
        }
        String userId = (String) firstWaitingUser.getValue();
        double score = firstWaitingUser.getScore();
        zSetOperations.remove(key2, userId);
        zSetOperations.add(key1, userId, score);
        return userId;
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
                Boolean added = zSetOperations.add(key, value, timestamp);
                if (Boolean.TRUE.equals(added)) {
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
            return false;
        } catch (Exception e) {
            log.error("❌ 큐 등록 중 오류 발생 - key: {}, error: {}", key, e.getMessage(), e);
            return false;
        } finally {
            // 락 해제 (반드시 finally에서 실행)
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("🔓 락 해제 완료 - key: {}", key);
            }
        }
    }

}
