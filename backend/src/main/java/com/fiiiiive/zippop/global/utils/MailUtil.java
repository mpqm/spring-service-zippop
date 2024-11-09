package com.fiiiiive.zippop.global.utils;

import com.fiiiiive.zippop.auth.model.dto.PostSignupRes;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

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

        message.setText("http://localhost:8080/api/v1/auth/verify?email="+response.getEmail()+"&role="+response.getRole()+"&uuid="+uuid);
        emailSender.send(message);
    }
}
