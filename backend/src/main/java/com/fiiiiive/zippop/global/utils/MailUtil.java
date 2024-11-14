package com.fiiiiive.zippop.global.utils;

import com.fiiiiive.zippop.auth.model.dto.PostSignupRes;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MailUtil {
    private final JavaMailSender emailSender;

    @Async
    public void sendSignupEmail(String uuid, PostSignupRes response) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(response.getEmail());
        if(Objects.equals(response.getRole(), "ROLE_COMPANY")){
            if(!response.getIsEmailAuth() && !response.getIsInactive()){
                message.setSubject("ZIPPOP - 기업으로 가입하신걸 환영합니다.");
            } else {
                message.setSubject("ZIPPOP - 기업회원계정 복구 이메일");
            }
        }
        else {
            if(!response.getIsEmailAuth() && !response.getIsInactive()){
                message.setSubject("ZIPPOP - 고객으로 가입하신걸 환영합니다.");
            } else {
                message.setSubject("ZIPPOP - 고객회원계정 복구 이메일");
            }
        }

        message.setText("http://localhost:8080/api/v1/auth/email-verify?email="+response.getEmail()+"&role="+response.getRole()+"&uuid="+uuid);
        emailSender.send(message);
    }

    @Async
    public void sendFindUserId(String email, String userId, Boolean isInActive) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("ZIPPOP - 아이디 찾기 결과입니다.");

        StringBuilder text = new StringBuilder("ID: " + userId + "\n");
        if (isInActive) {
            text.append("계정이 비활성화 되어 있어 다시 회원가입을 통해 이메일 인증 후 로그인해주세요.");
        } else {
            text.append("아이디 찾기 결과를 전송해드립니다. 비밀번호와 함께 로그인해주세요.");
        }

        message.setText(text.toString());
        emailSender.send(message);
    }

    @Async
    public void sendFindUserPassword(String email, String uuid, Boolean isInActive) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("ZIPPOP - 임시 비밀번호 발급 안내입니다.");

        StringBuilder text = new StringBuilder("임시 Password: " + uuid + "\n");
        if (isInActive) {
            text.append("계정이 비활성화 되어 있어 임시 비밀번호를 가지고 다시 회원가입을 통해 이메일 인증 후 로그인해주세요.");
        } else {
            text.append("임시 패스워드를 전송했습니다. 로그인 후 마이페이지에서 비밀번호를 변경해주세요.");
        }

        message.setText(text.toString());
        emailSender.send(message);
    }
}
