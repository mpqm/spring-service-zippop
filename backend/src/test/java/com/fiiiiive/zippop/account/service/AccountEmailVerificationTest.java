package com.fiiiiive.zippop.account.service;

import com.fiiiiive.zippop.account.model.entity.Account;
import com.fiiiiive.zippop.account.repository.AccountRepository;
import com.fiiiiive.zippop.account.repository.CompanyRepository;
import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.account.model.enums.RoleType;
import com.fiiiiive.zippop.global.redis.RedisAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AccountEmailVerificationTest {
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final AccountRepository accounts = mock(AccountRepository.class);
    private final RedisAccountService redis = mock(RedisAccountService.class);
    private final AccountService service = new AccountService(
        accounts,
            mock(PasswordEncoder.class),
            mock(AccountMailSender.class),
            redis,
            mock(CustomerRepository.class),
            mock(CompanyRepository.class),
            request
    );

    @Test
    void validUuidActivatesAccountConsumesKeyAndReturnsSuccessRedirect() {
        Account account = pending();
        request.setScheme("https");
        request.setServerName("shop.example.test");
        request.setServerPort(443);
        request.setContextPath("/zippop");
        when(accounts.findByEmail(account.getEmail())).thenReturn(Optional.of(account));
        when(redis.getEmailVerifyUuid(99L)).thenReturn("verify-id");

        assertThat(service.verifyAccount(account.getEmail(), "verify-id"))
                .isEqualTo("https://shop.example.test/zippop/login?success=true");
        assertThat(account.isEnabled()).isTrue();
        verify(redis).deleteEmailVerifyUuid(99L);
    }

    @Test
    void invalidUuidDoesNotActivateAccountOrConsumeVerificationKey() {
        Account account = pending();
        when(accounts.findByEmail(account.getEmail())).thenReturn(Optional.of(account));
        when(redis.getEmailVerifyUuid(99L)).thenReturn("actual-id");

        assertThat(service.verifyAccount(account.getEmail(), "wrong-id"))
                .isEqualTo("http://localhost/login?error=true");
        assertThat(account.isEnabled()).isFalse();
        verify(redis, never()).deleteEmailVerifyUuid(anyLong());
    }

    @Test
    void missingVerificationKeyDoesNotActivateAccountOrDeleteKey() {
        Account account = pending();
        when(accounts.findByEmail(account.getEmail())).thenReturn(Optional.of(account));
        when(redis.getEmailVerifyUuid(99L)).thenReturn(null);

        assertThat(service.verifyAccount(account.getEmail(), "verify-id"))
                .isEqualTo("http://localhost/login?error=true");
        assertThat(account.isEnabled()).isFalse();
        verify(redis, never()).deleteEmailVerifyUuid(anyLong());
    }

    @Test
    void missingAccountRedirectsToErrorWithoutReadingVerificationKey() {
        when(accounts.findByEmail("missing@example.test")).thenReturn(Optional.empty());

        assertThat(service.verifyAccount("missing@example.test", "verify-id"))
                .isEqualTo("http://localhost/login?error=true");
        verifyNoInteractions(redis);
    }

    private Account pending() {
        return Account.builder().idx(99L).email("member@example.test").role(RoleType.ROLE_CUSTOMER)
                .isEmailAuth(false).isInActive(true).build();
    }
}
