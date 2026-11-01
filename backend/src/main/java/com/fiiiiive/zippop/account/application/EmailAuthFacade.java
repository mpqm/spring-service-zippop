package com.fiiiiive.zippop.account.application;

import com.fiiiiive.zippop.account.service.AccountService;
import com.fiiiiive.zippop.account.service.CompanyService;
import com.fiiiiive.zippop.account.service.CustomerService;
import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import com.fiiiiive.zippop.global.enums.RoleType;
import com.fiiiiive.zippop.global.redis.RedisEmailService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

// 이메일 인증 Facade - Strategy 패턴을 활용한 역할별 서비스 라우팅
@Service
@RequiredArgsConstructor
public class EmailAuthFacade {

    private final RedisEmailService redisEmailService;
    private final CompanyService companyService;
    private final CustomerService customerService;
    private Map<String, AccountService> accountServiceMap;

    // 역할별 서비스 매핑 초기화 (Strategy 패턴)
    @PostConstruct
    private void init() {
        accountServiceMap = new HashMap<>();
        accountServiceMap.put(RoleType.ROLE_CUSTOMER.name(), customerService);
        accountServiceMap.put(RoleType.ROLE_COMPANY.name(), companyService);
    }

    // 역할별 서비스 분기
    private AccountService getAccountService(String role) throws ServiceException {
        AccountService service = accountServiceMap.get(role);
        if (service == null) {
            throw new ServiceException(ServiceErrorCode.AUTH_SIGNUP_FAIL_INVALID_ROLE_TYPE);
        }
        return service;
    }

    // 이메일 검증 및 계정 활성화
    @Transactional
    public boolean verifyEmail(String email, String role, String uuid) throws ServiceException {

        // 이메일에 해당하는 UUID 값 조회
        String storedUuid = redisEmailService.getEmailVerifyUuid(email);

        // URL 생성은 요청을 처리하는 컨트롤러에서 수행한다.
        if (storedUuid == null || !storedUuid.equals(uuid)) {
            return false;
        }

        // Strategy 패턴을 통한 역할별 서비스 호출
        getAccountService(role).activateAccount(email);

        // 인증 성공 후 Redis 에서 해당 이메일 관련 UUID 삭제
        redisEmailService.deleteEmailVerifyUuid(email);

        // 인증 결과 반환
        return true;
    }

}
