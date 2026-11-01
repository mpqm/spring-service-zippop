package com.fiiiiive.zippop.global.mail;

import com.fiiiiive.zippop.global.enums.RoleType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MailServiceTest {

    @Test
    void sendsTheResolvedVerificationUrlWithoutAccessingTheServletInTheMailWorker() {
        JavaMailSender sender = mock(JavaMailSender.class);
        String link = "https://shop.example.test/api/v1/accounts/verification"
                + "?email=member%40example.test&role=ROLE_CUSTOMER&uuid=verify-id";

        new MailService(sender).sendSignupEmail(link, "member@example.test", RoleType.ROLE_CUSTOMER, false);

        ArgumentCaptor<SimpleMailMessage> message = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(message.capture());
        assertThat(message.getValue().getText()).isEqualTo(link);
        assertThat(message.getValue().getTo()).containsExactly("member@example.test");
    }
}
