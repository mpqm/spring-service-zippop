package com.fiiiiive.zippop.account.policy;

import com.fiiiiive.zippop.account.repository.CompanyRepository;
import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import com.fiiiiive.zippop.global.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Company 도메인의 정책
@Component
@RequiredArgsConstructor
public class CompanyPolicy {

    private final CompanyRepository companyRepository;

    // 기업 회원 ID 중복 검증
    public void validateDuplicateUserId(String userId) throws ServiceException {
        if (companyRepository.findByUserId(userId).isPresent()) {
            throw new ServiceException(ServiceErrorCode.AUTH_SIGNUP_FAIL_ALREADY_EXIST_ID);
        }
    }

}
