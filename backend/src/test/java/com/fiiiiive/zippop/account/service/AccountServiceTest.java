package com.fiiiiive.zippop.account.service;

import com.fiiiiive.zippop.account.model.dto.CreateAccountReq;
import com.fiiiiive.zippop.account.model.dto.FindPwReq;
import com.fiiiiive.zippop.account.model.dto.ResetPwReq;
import com.fiiiiive.zippop.account.model.dto.ReplacePwReq;
import com.fiiiiive.zippop.account.model.entity.Account;
import com.fiiiiive.zippop.account.model.entity.Company;
import com.fiiiiive.zippop.account.model.entity.Customer;
import com.fiiiiive.zippop.account.repository.AccountRepository;
import com.fiiiiive.zippop.account.repository.CompanyRepository;
import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.account.model.enums.RoleType;
import com.fiiiiive.zippop.global.redis.RedisAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {
    private final AccountRepository accounts = mock(AccountRepository.class);
    private final CustomerRepository customers = mock(CustomerRepository.class);
    private final CompanyRepository companies = mock(CompanyRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final AccountMailSender mail = mock(AccountMailSender.class);
    private final RedisAccountService redis = mock(RedisAccountService.class);
    private final RequestUrlService urls = mock(RequestUrlService.class);
    private final MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    private final AccountService service = new AccountService(accounts, encoder, mail, redis, urls, customers, companies, servletRequest);

    @Test
    void existingRecoverableAccountOnlyResendsVerificationEmail() {
        Account pending = pendingAccount();
        when(accounts.findByEmail(pending.getEmail())).thenReturn(Optional.of(pending));
        when(redis.saveEmailVerifyUuid(eq(99L), anyString(), eq(3L))).thenReturn("verify-id");

        assertThat(service.createAccount(request(RoleType.ROLE_CUSTOMER), null).getIsExistUser()).isTrue();

        verify(accounts, never()).save(any());
        verify(mail).sendSignupEmail(
                "http://localhost/api/v1/accounts/verification?email=member%40example.test&uuid=verify-id",
                pending.getEmail(), RoleType.ROLE_CUSTOMER, true);
        verifyNoInteractions(customers, companies);
    }

    @Test
    void existingNonRecoverableEmailIsRejected() {
        Account account = Account.builder().userId("member01").email("member@example.test")
                .role(RoleType.ROLE_CUSTOMER).isEmailAuth(true).isInActive(false).build();
        when(accounts.findByEmail(account.getEmail())).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> service.createAccount(request(RoleType.ROLE_CUSTOMER), null))
                .isInstanceOf(ServiceException.class);
        verifyNoInteractions(redis, mail, customers, companies);
    }

    @Test
    void newCustomerCreatesProfileAndSendsVerificationEmail() {
        when(accounts.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            ReflectionTestUtils.setField(account, "idx", 99L);
            return account;
        });
        when(encoder.encode("pass1234")).thenReturn("encoded-password");
        when(redis.saveEmailVerifyUuid(eq(99L), anyString(), eq(3L))).thenReturn("verify-id");

        assertThat(service.createAccount(request(RoleType.ROLE_CUSTOMER), null).getIsExistUser()).isFalse();

        var profile = org.mockito.ArgumentCaptor.forClass(Customer.class);
        verify(customers).save(profile.capture());
        assertThat(profile.getValue().getPoint()).isEqualTo(3000);
        assertThat(profile.getValue().getAccount().getPassword()).isEqualTo("encoded-password");
        verify(mail).sendSignupEmail(
                "http://localhost/api/v1/accounts/verification?email=member%40example.test&uuid=verify-id",
                "member@example.test", RoleType.ROLE_CUSTOMER, true);
        verifyNoInteractions(companies);
    }

    @Test
    void newCompanyStoresCrnWithoutAddingCrnValidation() {
        when(accounts.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            ReflectionTestUtils.setField(account, "idx", 99L);
            return account;
        });
        when(redis.saveEmailVerifyUuid(eq(99L), anyString(), eq(3L))).thenReturn("verify-id");

        service.createAccount(request(RoleType.ROLE_COMPANY), null);

        var profile = org.mockito.ArgumentCaptor.forClass(Company.class);
        verify(companies).save(profile.capture());
        assertThat(profile.getValue().getCrn()).isEqualTo("1234567890");
        verifyNoInteractions(customers);
    }

    @Test
    void findPasswordBuildsResetLinkFromTheCurrentRequest() {
        Account account = pendingAccount();
        when(accounts.findByUserId("member01")).thenReturn(Optional.of(account));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("https");
        request.setServerName("shop.example.test");
        request.setServerPort(8443);
        request.setContextPath("/zippop");

        service.findPw(FindPwReq.builder().role(RoleType.ROLE_CUSTOMER.name())
                .userId("member01").build());

        var token = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(redis).savePasswordResetToken(token.capture(), eq(99L), eq(10L));
        verify(mail).sendPasswordResetLink(eq(account.getEmail()),
                contains("https://shop.example.test:8443/zippop/password-reset?token=" + token.getValue()));
    }

    @Test
    void resetPasswordConsumesTheTokenAndChangesThePassword() {
        Account account = Account.builder().idx(99L).password("old-hash")
                .isEmailAuth(true).isInActive(false).build();
        when(redis.consumePasswordResetToken("one-time-token")).thenReturn(99L);
        when(accounts.findById(99L)).thenReturn(Optional.of(account));
        when(encoder.encode("new-password")).thenReturn("new-hash");

        service.resetPw(ResetPwReq.builder().token("one-time-token").newPassword("new-password").build());

        assertThat(account.getPassword()).isEqualTo("new-hash");
        assertThat(account.getAuthVersion()).isEqualTo(1);
    }

    @Test
    void loggedInPasswordChangeChecksTheOriginalPassword() {
        Account account = Account.builder().idx(99L).password("old-hash")
                .isEmailAuth(true).isInActive(false).build();
        CustomUserDetails user = CustomUserDetails.builder().accountId(99L).build();
        when(accounts.findById(99L)).thenReturn(Optional.of(account));
        when(encoder.matches("old-password", "old-hash")).thenReturn(true);
        when(encoder.encode("new-password")).thenReturn("new-hash");

        service.replacePw(user, ReplacePwReq.builder()
                .originPassword("old-password").newPassword("new-password").build());

        assertThat(account.getPassword()).isEqualTo("new-hash");
        assertThat(account.getAuthVersion()).isEqualTo(1);
    }

    private CreateAccountReq request(RoleType role) {
        return CreateAccountReq.builder().role(role).userId("member01").email("member@example.test")
                .password("pass1234").name("Member").phoneNumber("01012345678").address("Seoul")
                .crn("1234567890").build();
    }

    private Account pendingAccount() {
        return Account.builder().idx(99L).userId("member01").email("member@example.test")
                .role(RoleType.ROLE_CUSTOMER).isEmailAuth(false).isInActive(true).build();
    }
}
