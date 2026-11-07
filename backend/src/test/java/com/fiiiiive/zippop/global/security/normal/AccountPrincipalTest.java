package com.fiiiiive.zippop.global.security.normal;

import com.fiiiiive.zippop.account.model.entity.*;
import com.fiiiiive.zippop.account.repository.*;
import com.fiiiiive.zippop.account.model.enums.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountPrincipalTest {
    @Test void customerIdentityDoesNotUseAccountIdAsCustomerId() {
        var accounts = mock(AccountRepository.class);
        var customers = mock(CustomerRepository.class);
        var companies = mock(CompanyRepository.class);
        Account account = account(RoleType.ROLE_CUSTOMER);
        when(accounts.findByUserId("member01")).thenReturn(Optional.of(account));
        when(customers.findByAccountIdx(99L)).thenReturn(Optional.of(Customer.builder().idx(3L).account(account).build()));
        var user = new CustomUserDetailService(accounts, customers, companies).loadUserByUsername("member01");
        assertThat(user.getAccountId()).isEqualTo(99L);
        assertThat(user.getCustomerId()).isEqualTo(3L);
        assertThatThrownBy(user::getCompanyId).isInstanceOf(AccessDeniedException.class);
        verifyNoInteractions(companies);
    }
    @Test void companyIdentityDoesNotUseAccountIdAsCompanyId() {
        var accounts = mock(AccountRepository.class);
        var customers = mock(CustomerRepository.class);
        var companies = mock(CompanyRepository.class);
        Account account = account(RoleType.ROLE_COMPANY);
        when(accounts.findById(99L)).thenReturn(Optional.of(account));
        when(companies.findByAccountIdx(99L)).thenReturn(Optional.of(Company.builder().idx(2L).account(account).build()));
        var user = new CustomUserDetailService(accounts, customers, companies).loadByAccountId(99L);
        assertThat(user.getCompanyId()).isEqualTo(2L);
        assertThatThrownBy(user::getCustomerId).isInstanceOf(AccessDeniedException.class);
        verifyNoInteractions(customers);
    }
    private Account account(RoleType role) {
        return Account.builder().idx(99L).userId("member01").email("member@example.test").role(role)
                .isEmailAuth(true).isInActive(false).build();
    }
}
