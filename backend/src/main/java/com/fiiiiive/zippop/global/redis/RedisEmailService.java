package com.fiiiiive.zippop.global.redis;

import com.fiiiiive.zippop.global.base.ServerErrorCode;
import com.fiiiiive.zippop.global.base.ServerException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class RedisEmailService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 이메일 인증 UUID 저장 (key: email, value: uuid, expirationTime: 3 minutes)
    public String saveEmailVerifyUuid(String email, String uuid, long expirationTimeMinutes) {
        try {
            redisTemplate.opsForValue().set("emailVerify:" + email, uuid, expirationTimeMinutes, TimeUnit.MINUTES);
            return uuid;
        } catch (Exception e) {
            log.error("이메일 인증 UUID 저장 중 오류: {}", e.getMessage());
            throw new ServerException(ServerErrorCode.REDIS_SAVE_ERROR, e);
        }
    }

    // 이메일 인증 UUID 조회
    public String getEmailVerifyUuid(String email) {
        try {
            Object value = redisTemplate.opsForValue().get("emailVerify:" + email);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.error("이메일 인증 UUID 조회 중 오류: {}", e.getMessage());
            throw new ServerException(ServerErrorCode.REDIS_READ_ERROR, e);
        }
    }

    // 이메일 인증 UUID 삭제
    public void deleteEmailVerifyUuid(String email) {
        try {
            redisTemplate.delete("emailVerify:" + email);
        } catch (Exception e) {
            log.error("이메일 인증 UUID 삭제 중 오류: {}", e.getMessage());
            throw new ServerException(ServerErrorCode.REDIS_DELETE_ERROR, e);
        }
    }

}
