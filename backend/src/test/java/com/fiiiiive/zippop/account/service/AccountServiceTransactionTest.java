package com.fiiiiive.zippop.account.service;

import com.fiiiiive.zippop.account.model.dto.CreateAccountReq;
import com.fiiiiive.zippop.account.model.dto.UpdateAccountReq;
import com.fiiiiive.zippop.account.model.entity.Account;
import com.fiiiiive.zippop.account.repository.AccountRepository;
import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.account.model.enums.RoleType;
import com.fiiiiive.zippop.global.redis.RedisAccountService;
import com.fiiiiive.zippop.global.security.PasswordConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@DataJpaTest(properties = {"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"})
@Import({AccountService.class, PasswordConfig.class})
class AccountServiceTransactionTest {
    @Autowired AccountService service;
    @Autowired AccountRepository accounts;
    @Autowired CustomerRepository customers;
    @Autowired jakarta.persistence.EntityManager entityManager;
    @MockBean
    AccountMailSender mail;
    @MockBean
    RedisAccountService redis;
    @MockBean RequestUrlService urls;
    @MockBean jakarta.servlet.http.HttpServletRequest request;

    @Test
    void customerRegistrationPersistsTheAccountAndProfile() {
        long before = accounts.count();
        when(redis.saveEmailVerifyUuid(anyLong(), any(), anyLong())).thenReturn("verify-id");
        stubRequestUrl();

        service.createAccount(request(RoleType.ROLE_CUSTOMER), null);

        assertThat(accounts.count()).isEqualTo(before + 1);
        assertThat(customers.count()).isEqualTo(1);
    }

    @Test
    void companyUpdatePersistsCommonFieldsCrnAndKeepsExistingImage() {
        when(redis.saveEmailVerifyUuid(anyLong(), any(), anyLong())).thenReturn("verify-id");
        stubRequestUrl();
        service.createAccount(request(RoleType.ROLE_COMPANY), "original-image");
        Account created = accounts.findByUserId("member01").orElseThrow();

        service.updateAccount(created.getIdx(), UpdateAccountReq.builder().name("Updated")
                .address("Busan").phoneNumber("01087654321").crn("9876543210").build(), null);
        entityManager.flush();
        entityManager.clear();

        var result = service.getAccount(created.getIdx());
        assertThat(result.getName()).isEqualTo("Updated");
        assertThat(result.getAddress()).isEqualTo("Busan");
        assertThat(result.getCrn()).isEqualTo("9876543210");
        assertThat(result.getProfileImageUrl()).isEqualTo("original-image");
    }

    private CreateAccountReq request(RoleType role) {
        return CreateAccountReq.builder().role(role).userId("member01").email("member@example.test")
                .password("pass1234").name("Member").phoneNumber("01012345678").address("Seoul")
                .crn("1234567890").build();
    }

    private void stubRequestUrl() {
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("example.test");
        when(request.getServerPort()).thenReturn(443);
        when(request.getContextPath()).thenReturn("");
    }
}
