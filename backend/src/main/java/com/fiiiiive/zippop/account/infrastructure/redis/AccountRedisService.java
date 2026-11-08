package com.fiiiiive.zippop.account.infrastructure.redis;

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
public class AccountRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 이메일 인증 UUID 저장 (key: email, value: uuid, expirationTime: 3 minutes)
    public String saveEmailVerifyUuid(Long accountId, String uuid, long expirationTimeMinutes) {
        try {
            redisTemplate.opsForValue().set("emailVerify:v2:" + accountId, uuid, expirationTimeMinutes, TimeUnit.MINUTES);
            return uuid;
        } catch (Exception e) {
            log.error("이메일 인증 UUID 저장 중 오류: {}", e.getMessage());
            throw new ServerException(ServerErrorCode.REDIS_SAVE_ERROR, e);
        }
    }

    // 이메일 인증 UUID 조회
    public String getEmailVerifyUuid(Long accountId) {
        try {
            Object value = redisTemplate.opsForValue().get("emailVerify:v2:" + accountId);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.error("이메일 인증 UUID 조회 중 오류: {}", e.getMessage());
            throw new ServerException(ServerErrorCode.REDIS_READ_ERROR, e);
        }
    }

    // 이메일 인증 UUID 삭제
    public void deleteEmailVerifyUuid(Long accountId) {
        try {
            redisTemplate.delete("emailVerify:v2:" + accountId);
        } catch (Exception e) {
            log.error("이메일 인증 UUID 삭제 중 오류: {}", e.getMessage());
            throw new ServerException(ServerErrorCode.REDIS_DELETE_ERROR, e);
        }
    }

    // 패스워드 리셋 토큰 저장
    public void savePasswordResetToken(String token, Long accountId, long expirationTimeMinutes) {
        try {
            redisTemplate.opsForValue().set("passwordReset:" + token, accountId.toString(), expirationTimeMinutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("비밀번호 재설정 토큰 저장 중 오류: {}", e.getMessage());
            throw new ServerException(ServerErrorCode.REDIS_SAVE_ERROR, e);
        }
    }

    // 패스워드 리셋 토큰 조회(소비하지 않음)
    public Long getPasswordResetAccountId(String token) {
        try {
            Object value = redisTemplate.opsForValue().get("passwordReset:" + token);
            return value == null ? null : Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            log.warn("잘못된 비밀번호 재설정 토큰 값이 저장되어 있습니다.");
            return null;
        } catch (Exception e) {
            log.error("비밀번호 재설정 토큰 조회 중 오류: {}", e.getMessage());
            throw new ServerException(ServerErrorCode.REDIS_READ_ERROR, e);
        }
    }

    // 패스워드 리셋 토큰 소비
    public Long consumePasswordResetToken(String token) {
        try {
            Object value = redisTemplate.opsForValue().getAndDelete("passwordReset:" + token);
            return value == null ? null : Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            log.warn("잘못된 비밀번호 재설정 토큰 값이 저장되어 있습니다.");
            return null;
        } catch (Exception e) {
            log.error("비밀번호 재설정 토큰 소비 중 오류: {}", e.getMessage());
            throw new ServerException(ServerErrorCode.REDIS_READ_ERROR, e);
        }
    }

}
