package com.fiiiiive.zippop.global.mail;

import com.fiiiiive.zippop.global.base.BaseConstant;
import com.fiiiiive.zippop.global.base.ServerErrorCode;
import com.fiiiiive.zippop.global.base.ServerException;
import com.fiiiiive.zippop.global.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;

    // 회원가입 이메일 인증 전송(비동기)
    @Async
    public void sendSignupEmail(String uuid, String email, RoleType role, Boolean recoveryTarget) {

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
        message.setText(BaseConstant.DEFAULT_SERVER_URL+"/api/v1/auth/verify?email="+email+"&role="+role+"&uuid="+uuid);
        try {
            emailSender.send(message);
        } catch (MailException exception) {
            throw new ServerException(ServerErrorCode.EMAIL_SEND_ERROR, exception);
        }

    }

    // 아이디 찾기 이메일 전송
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

    // 비밀번호 찾기 이메일 전송
    @Async
    public void sendFindUserPassword(String email, String uuid, Boolean isInActive) {

        // 이메일 수신자, 제목 설정
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("ZIPPOP - 임시 비밀번호 발급 안내입니다.");

        // if : 비활성화 회원 임시 패스워드 전송
        // else : 활성화 회원 임시 패스워드 전송
        StringBuilder text = new StringBuilder("임시 Password: " + uuid + "\n");
        if (isInActive) text.append("계정이 비활성화 되어 있어 계정 활성화 후 임시 비밀번호로 로그인해주세요.");
        else text.append("임시 패스워드를 전송했습니다. 로그인 후 마이페이지에서 비밀번호를 변경해주세요.");

        // 메시지 생성 및 전송
        message.setText(text.toString());
        try {
            emailSender.send(message);
        } catch (MailException exception) {
            throw new ServerException(ServerErrorCode.EMAIL_SEND_ERROR, exception);
        }

    }
}
