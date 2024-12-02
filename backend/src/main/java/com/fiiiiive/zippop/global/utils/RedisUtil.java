package com.fiiiiive.zippop.global.utils;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    // SortedSet 생성 초기화 및 만료 시간 설정
    public void create(String key, long expirationTimeMinutes) {
        try {
            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
            zSetOperations.add(key, "start", System.currentTimeMillis());
            zSetOperations.remove(key, "start");
            redisTemplate.expire(key, expirationTimeMinutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // SortedSet 값 저장(key, value, score), 타임스탬프를 기준으로 value를 저장 및 정렬
    public void save(String key, String value, long timestamp) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(key, value, timestamp);
    }

    // SortedSet 전체 값 조회
    public String getAllValues(String key) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> values = zSetOperations.rangeWithScores(key, 0, -1);
        values.forEach( value -> log.info("Value: " + value.getValue() + ", Score: " + value.getScore()) );
        String total = String.valueOf(this.getSize(key));
        log.info(total);
        log.info("==========================================");
        return total;
    }

    // SortedSet 전체 사이즈 조회
    public Long getSize(String key) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.zCard(key);
    }

    // SortedSet에서 값 갱신 (기존의 value에 대해 새로운 score로 갱신)
    public void update(String key, String value, long newTimestamp) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        boolean exists = zSetOperations.remove(key, value) > 0; // 먼저 기존 값을 삭제
        if (exists) {
            zSetOperations.add(key, value, newTimestamp);
        }
    }

    // SortedSet 내 값의 순위 조회
    public Long getOrder(String key, String value) throws BaseException {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.rank(key, value);
    }

    // SortedSet 값 삭제
    public void remove(String key, String value) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.remove(key, value);
    }

    public String firstWaitingUserToWorking(String key1, String key2, Integer fixedSize) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> waitingList = zSetOperations.rangeWithScores(key2, 0, 0);
        if (waitingList != null && !waitingList.isEmpty() && getSize(key1) < fixedSize) {
            ZSetOperations.TypedTuple<Object> firstWaitingUser = waitingList.iterator().next();
            String userId = (String) firstWaitingUser.getValue();
            double score = firstWaitingUser.getScore();
            zSetOperations.remove(key2, userId);
            zSetOperations.add(key1, userId, score);
            return userId;
        } else {
            return null;
        }
    }
}
