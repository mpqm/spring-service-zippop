package com.fiiiiive.zippop.account.policy;

import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Customer 도메인의 정책 구현
@Component
@RequiredArgsConstructor
public class CustomerPolicy {

    private final CustomerRepository customerRepository;

    // 고객 회원 ID 중복 검증
    public void validateDuplicateUserId(String userId) throws ServiceException {
        if (customerRepository.findByUserId(userId).isPresent()) {
            throw new ServiceException(ServiceErrorCode.AUTH_SIGNUP_FAIL_ALREADY_EXIST_ID);
        }
    }

}
