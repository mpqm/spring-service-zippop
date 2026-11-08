package com.fiiiiive.zippop.account.infrastructure.mail;

import com.fiiiiive.zippop.account.model.enums.RoleType;
import com.fiiiiive.zippop.global.base.ServerErrorCode;
import com.fiiiiive.zippop.global.base.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AccountMailService {

    private final JavaMailSender emailSender;

    // 회원가입 이메일 인증 전송(비동기)
    @Async
    public void sendSignupEmail(String verificationUrl, String email, RoleType role, Boolean recoveryTarget) {

        // 이메일 수신자 설정
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);

        // if: 기업회원 이메일 제목 설정
        // else: 고객회원 이메일 제목 설정
        if(Objects.equals(role, RoleType.ROLE_COMPANY)) {

            // if: isEmailAuth(0) && isInActive(1) 비활성화 기업회원 복구 이메일 전송
            // else: isEmailAuth(0) && isInActive(0) 신규 기업회원 이메일 전송 및 이메일 인증 재전송
            if(recoveryTarget) message.setSubject("ZIPPOP - 기업회원계정 복구 이메일 인증 입니다.");
            else message.setSubject("ZIPPOP - 기업으로 가입하신걸 환영 합니다.");

        } else {

            // if: isEmailAuth(0) && isInActive(1) 비활성화 고객회원 복구 이메일 전송
            // else: isEmailAuth(0) && isInActive(0) 신규 고객회원 이메일 전송 및 이메일 인증 재전송
            if(recoveryTarget) message.setSubject("ZIPPOP - 고객회원계정 복구 이메일 인증 입니다.");
            else message.setSubject("ZIPPOP - 고객으로 가입하신걸 환영 합니다.");
        }

        // 메시지 생성 및 전송
        message.setText(verificationUrl);
        try {
            emailSender.send(message);
        } catch (MailException exception) {
            throw new ServerException(ServerErrorCode.EMAIL_SEND_ERROR, exception);
        }

    }

    // 아이디 찾기 이메일 전송(비동기)
    @Async
    public void sendFindUserId(String email, String userId, Boolean isInActive) {

        // 이메일 수신자, 제목 설정
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("ZIPPOP - 아이디 찾기 결과입니다.");

        // if : 비활성화 회원 ID 전송
        // else : 활성화 회원 ID 전송
        StringBuilder text = new StringBuilder("ID: " + userId + "\n");
        if (isInActive) text.append("계정이 비활성화 되어 있어 계정 활성화 후 로그인해주세요");
        else text.append("아이디 찾기 결과를 전송해드립니다. 비밀번호와 함께 로그인해주세요.");

        // 메시지 생성 및 전송
        message.setText(text.toString());
        try {
            emailSender.send(message);
        } catch (MailException exception) {
            throw new ServerException(ServerErrorCode.EMAIL_SEND_ERROR, exception);
        }

    }

    // 비밀번호 리셋 이메일 전송(비동기)
    @Async
    public void sendPasswordResetLink(String email, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("ZIPPOP - 비밀번호 재설정 안내입니다.");
        message.setText("아래 일회용 링크에서 비밀번호를 재설정해주세요. 링크는 10분 동안 유효합니다.\n" + resetUrl);
        try {
            emailSender.send(message);
        } catch (MailException exception) {
            throw new ServerException(ServerErrorCode.EMAIL_SEND_ERROR, exception);
        }
    }

}
