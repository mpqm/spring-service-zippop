package com.fiiiiive.zippop.global.security.normal;

import com.fiiiiive.zippop.account.model.entity.Account;
import com.fiiiiive.zippop.account.repository.*;
import com.fiiiiive.zippop.account.model.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class CustomUserDetailService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;

    @Override public CustomUserDetails loadUserByUsername(String userId) {
        return principal(accountRepository.findByUserId(userId).orElseThrow(this::notFound));
    }
    public CustomUserDetails loadByAccountId(Long accountId) {
        return principal(accountRepository.findById(accountId).orElseThrow(this::notFound));
    }
    private CustomUserDetails principal(Account account) {
        if (account.getRole() == RoleType.ROLE_CUSTOMER) {
            Long id = customerRepository.findByAccountIdx(account.getIdx()).orElseThrow(this::notFound).getIdx();
            return CustomUserDetails.from(account, id, null);
        }
        Long id = companyRepository.findByAccountIdx(account.getIdx()).orElseThrow(this::notFound).getIdx();
        return CustomUserDetails.from(account, null, id);
    }
    private UsernameNotFoundException notFound() { return new UsernameNotFoundException("계정을 찾을 수 없습니다."); }
}
