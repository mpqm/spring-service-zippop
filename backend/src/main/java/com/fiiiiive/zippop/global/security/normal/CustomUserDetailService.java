package com.fiiiiive.zippop.global.security.normal;

import com.fiiiiive.zippop.account.model.Company;
import com.fiiiiive.zippop.account.model.Customer;
import com.fiiiiive.zippop.account.repository.CompanyRepository;
import com.fiiiiive.zippop.account.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<Customer> resultCustomer = customerRepository.findByUserId(userId);
        if (resultCustomer.isPresent()) {
            Customer customer = resultCustomer.get();
            return CustomUserDetails.builder()
                    .idx(customer.getIdx())
                    .userId(customer.getUserId())
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .password(customer.getPassword())
                    .role(customer.getRole().getName())
                    .isEmailAuth(customer.getIsEmailAuth())
                    .build();
        }
        Optional<Company> resultCompany = companyRepository.findByUserId(userId);
        if (resultCompany.isPresent()) {
            Company company = resultCompany.get();
            return CustomUserDetails.builder()
                    .idx(company.getIdx())
                    .userId(company.getUserId())
                    .name(company.getName())
                    .email(company.getEmail())
                    .password(company.getPassword())
                    .role(company.getRole().getName())
                    .isEmailAuth(company.getIsEmailAuth())
                    .build();
        } else {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
    }

}