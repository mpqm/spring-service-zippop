package com.fiiiiive.zippop.account.application;

import com.fiiiiive.zippop.account.model.entity.Account;
import com.fiiiiive.zippop.global.mail.MailService;
import com.fiiiiive.zippop.global.redis.RedisEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmailAuthSender {

    private final MailService mailService;
    private final RedisEmailService redisEmailService;

    // 이메일 인증 비동기 전송 + 인증 유효 기간 설정 메서드
    @Transactional
    public void sendEmailAuth(Account account) {

        // Redis에 랜덤 UUID와 이메일 저장, 유효 기간 3분으로 설정
        String uuid = redisEmailService.saveEmailVerifyUuid(account.getEmail(), UUID.randomUUID().toString(), 3);

        // 회원가입 인증 이메일 전송
        mailService.sendSignupEmail(uuid, account.getEmail(), account.getRole(), account.isRecoveryTarget());

    }
}
