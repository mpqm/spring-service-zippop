package com.fiiiiive.zippop.account.policy;

import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Customer 도메인의 정책 구현
 * DDD: Policy Pattern - 고객 회원 관련 비즈니스 규칙과 검증 로직
 * 
 * 책임:
 * 1. 고객 회원 ID 중복 검증
 * 2. ROLE_CUSTOMER 역할 검증
 * 3. 고객 특화 도메인 규칙 적용
 */
@Component
@RequiredArgsConstructor
public class CustomerPolicy {

    private final CustomerRepository customerRepository;

    public void validateDuplicateUserId(String userId) throws BaseException {
        if (customerRepository.findByUserId(userId).isPresent()) {
            throw new BaseException(BaseMessage.AUTH_SIGNUP_FAIL_ALREADY_EXIST_ID);
        }
    }

    public void validateRoleType(RoleType roleType) throws BaseException {
        if (roleType != RoleType.ROLE_CUSTOMER) {
            throw new BaseException(BaseMessage.AUTH_SIGNUP_FAIL_INVALID_ROLE);
        }
    }
}
