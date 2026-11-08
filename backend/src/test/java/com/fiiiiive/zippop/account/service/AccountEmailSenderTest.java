package com.fiiiiive.zippop.account.service;

import com.fiiiiive.zippop.account.infrastructure.mail.AccountMailService;
import com.fiiiiive.zippop.account.infrastructure.redis.AccountRedisService;
import com.fiiiiive.zippop.account.model.entity.Account;
import com.fiiiiive.zippop.account.repository.AccountRepository;
import com.fiiiiive.zippop.account.repository.CompanyRepository;
import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.account.model.enums.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountEmailSenderTest {
    @Test
    void sendsVerificationLinkWithoutRoleParameter() {
        AccountMailService mail = mock(AccountMailService.class);
        AccountRedisService redis = mock(AccountRedisService.class);
        when(redis.saveEmailVerifyUuid(eq(99L), anyString(), eq(3L))).thenReturn("verify-id");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("https");
        request.setServerName("shop.example.test");
        request.setServerPort(443);

        AccountService service = new AccountService(
                mock(AccountRepository.class), mock(PasswordEncoder.class), mail, redis,
                mock(CustomerRepository.class), mock(CompanyRepository.class), request);
        Account account = Account.builder().idx(99L).email("member+tag@example.test")
                .role(RoleType.ROLE_CUSTOMER).isEmailAuth(false).isInActive(true).build();

        service.sendEmailAuth(account);

        verify(mail).sendSignupEmail(
                "https://shop.example.test/api/v1/accounts/verification"
                        + "?email=member%2Btag%40example.test&uuid=verify-id",
                "member+tag@example.test", RoleType.ROLE_CUSTOMER, true);
    }
}
