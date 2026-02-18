package com.fiiiiive.zippop.account.policy;

import com.fiiiiive.zippop.account.repository.CompanyRepository;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Company 도메인의 정책
@Component
@RequiredArgsConstructor
public class CompanyPolicy {

    private final CompanyRepository companyRepository;

    // 기업 회원 ID 중복 검증
    public void validateDuplicateUserId(String userId) throws BaseException {
        if (companyRepository.findByUserId(userId).isPresent()) {
            throw new BaseException(BaseMessage.AUTH_SIGNUP_FAIL_ALREADY_EXIST_ID);
        }
    }

}
