package com.fiiiiive.zippop.account.repository;

import com.fiiiiive.zippop.account.model.entity.*;
import com.fiiiiive.zippop.account.model.enums.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest(properties = {"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"})
class AccountPersistenceTest {
    @Autowired AccountRepository accounts;
    @Autowired CustomerRepository customers;
    @Autowired CompanyRepository companies;

    @Test void profilesKeepTheirOwnIdsAndResolveThroughASeparateAccountId() {
        accounts.saveAndFlush(account("unused", "unused@example.test", RoleType.ROLE_CUSTOMER));
        Account account = accounts.saveAndFlush(account("customer01", "customer@example.test", RoleType.ROLE_CUSTOMER));
        Customer customer = customers.saveAndFlush(Customer.builder().account(account).point(3000).build());
        assertThat(customer.getIdx()).isNotEqualTo(account.getIdx());
        assertThat(customers.findByAccountIdx(account.getIdx())).get().extracting(Customer::getIdx).isEqualTo(customer.getIdx());
        assertThat(customers.findByCustomerIdx(customer.getIdx())).get().extracting(Customer::getEmail).isEqualTo("customer@example.test");
    }
    @Test void theSameLoginIdCannotBeInsertedForAnotherRole() {
        accounts.saveAndFlush(account("shared01", "customer@example.test", RoleType.ROLE_CUSTOMER));
        assertThatThrownBy(() -> accounts.saveAndFlush(account("shared01", "company@example.test", RoleType.ROLE_COMPANY)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
    @Test void theSameEmailCannotBeInsertedForAnotherRole() {
        accounts.saveAndFlush(account("customer01", "shared@example.test", RoleType.ROLE_CUSTOMER));
        assertThatThrownBy(() -> accounts.saveAndFlush(account("company01", "shared@example.test", RoleType.ROLE_COMPANY)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
    @Test void anAccountCannotHaveTwoCustomerProfiles() {
        Account account = accounts.saveAndFlush(account("customer01", "customer@example.test", RoleType.ROLE_CUSTOMER));
        customers.saveAndFlush(Customer.builder().account(account).point(3000).build());
        assertThatThrownBy(() -> customers.saveAndFlush(Customer.builder().account(account).point(3000).build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
    private Account account(String userId, String email, RoleType role) {
        return Account.builder().userId(userId).email(email).role(role).password("unchanged-hash")
                .name("회원").phoneNumber("01012345678").address("서울")
                .isEmailAuth(true).isInActive(false).build();
    }
}
