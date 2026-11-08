package com.fiiiiive.zippop.account.service;

import com.fiiiiive.zippop.account.infrastructure.mail.AccountMailService;
import com.fiiiiive.zippop.account.model.enums.RoleType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AccountMailServiceTest {

    @Test
    void sendsTheResolvedVerificationUrlWithoutAccessingTheServletInTheMailWorker() {
        JavaMailSender sender = mock(JavaMailSender.class);
        String link = "https://shop.example.test/api/v1/accounts/verification"
                + "?email=member%40example.test&role=ROLE_CUSTOMER&uuid=verify-id";

        new AccountMailService(sender).sendSignupEmail(link, "member@example.test", RoleType.ROLE_CUSTOMER, false);

        ArgumentCaptor<SimpleMailMessage> message = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(message.capture());
        assertThat(message.getValue().getText()).isEqualTo(link);
        assertThat(message.getValue().getTo()).containsExactly("member@example.test");
    }
}
