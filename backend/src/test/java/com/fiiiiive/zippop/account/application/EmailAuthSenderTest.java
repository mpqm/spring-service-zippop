package com.fiiiiive.zippop.account.application;

import com.fiiiiive.zippop.account.model.entity.Customer;
import com.fiiiiive.zippop.global.enums.RoleType;
import com.fiiiiive.zippop.global.mail.MailService;
import com.fiiiiive.zippop.global.redis.RedisEmailService;
import com.fiiiiive.zippop.global.web.RequestUrlService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailAuthSenderTest {

    @Test
    void passesAFullyBuiltVerificationLinkToTheAsyncMailService() {
        MailService mail = mock(MailService.class);
        RedisEmailService redis = mock(RedisEmailService.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("https");
        request.setServerName("shop.example.test");
        request.setServerPort(443);
        when(redis.saveEmailVerifyUuid(eq("member+tag@example.test"), anyString(), eq(3L)))
                .thenReturn("verify-id");
        Customer account = Customer.builder()
                .email("member+tag@example.test")
                .role(RoleType.ROLE_CUSTOMER)
                .isEmailAuth(false)
                .isInActive(true)
                .build();

        new EmailAuthSender(mail, redis, new RequestUrlService(request)).sendEmailAuth(account);

        verify(mail).sendSignupEmail(
                "https://shop.example.test/api/v1/accounts/verification"
                        + "?email=member%2Btag%40example.test&role=ROLE_CUSTOMER&uuid=verify-id",
                "member+tag@example.test", RoleType.ROLE_CUSTOMER, true);
    }
}
