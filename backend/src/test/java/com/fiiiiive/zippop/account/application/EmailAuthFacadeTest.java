package com.fiiiiive.zippop.account.application;

import com.fiiiiive.zippop.account.service.CompanyService;
import com.fiiiiive.zippop.account.service.CustomerService;
import com.fiiiiive.zippop.global.redis.RedisEmailService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class EmailAuthFacadeTest {

    @Test
    void validUuidActivatesTheAccountAndIsConsumed() {
        RedisEmailService redis = mock(RedisEmailService.class);
        CustomerService customer = mock(CustomerService.class);
        EmailAuthFacade facade = new EmailAuthFacade(redis, mock(CompanyService.class), customer);
        ReflectionTestUtils.invokeMethod(facade, "init");
        when(redis.getEmailVerifyUuid("member@example.test")).thenReturn("verify-id");

        assertThat(facade.verifyEmail("member@example.test", "ROLE_CUSTOMER", "verify-id")).isTrue();

        verify(customer).activateAccount("member@example.test");
        verify(redis).deleteEmailVerifyUuid("member@example.test");
    }

    @Test
    void invalidUuidDoesNotActivateTheAccountOrConsumeAnotherLink() {
        RedisEmailService redis = mock(RedisEmailService.class);
        CustomerService customer = mock(CustomerService.class);
        CompanyService company = mock(CompanyService.class);
        EmailAuthFacade facade = new EmailAuthFacade(redis, company, customer);
        when(redis.getEmailVerifyUuid("member@example.test")).thenReturn("actual-id");

        assertThat(facade.verifyEmail("member@example.test", "ROLE_CUSTOMER", "wrong-id")).isFalse();

        verifyNoInteractions(customer, company);
        verify(redis, never()).deleteEmailVerifyUuid("member@example.test");
    }
}
