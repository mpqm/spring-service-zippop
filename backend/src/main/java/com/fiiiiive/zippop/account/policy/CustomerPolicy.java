package com.fiiiiive.zippop.account.policy;

import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Customer 도메인의 정책 구현
@Component
@RequiredArgsConstructor
public class CustomerPolicy {

    private final CustomerRepository customerRepository;

    public void validateDuplicateUserId(String userId) throws BaseException {
        if (customerRepository.findByUserId(userId).isPresent()) {
            throw new BaseException(BaseMessage.AUTH_SIGNUP_FAIL_ALREADY_EXIST_ID);
        }
    }

}
