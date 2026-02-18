package com.fiiiiive.zippop.account.application;

import com.fiiiiive.zippop.account.model.AccountDto;
import com.fiiiiive.zippop.account.service.AccountService;
import com.fiiiiive.zippop.account.service.CompanyService;
import com.fiiiiive.zippop.account.service.CustomerService;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.enums.RoleType;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// Account Facade - Strategy 패턴을 활용한 역할별 서비스 라우팅
@Service
@RequiredArgsConstructor
public class AccountFacade {

    private final CustomerService customerService;
    private final CompanyService companyService;
    private Map<String, AccountService> accountServiceMap;

    // 역할별 서비스 매핑 초기화 (Strategy 패턴)
    @PostConstruct
    private void init() {
        accountServiceMap = new HashMap<>();
        accountServiceMap.put(RoleType.ROLE_CUSTOMER.name(), customerService);
        accountServiceMap.put(RoleType.ROLE_COMPANY.name(), companyService);
    }

    private AccountService getAccountService(String role) throws BaseException {
        AccountService service = accountServiceMap.get(role);
        if (service == null) {
            throw new BaseException(BaseMessage.AUTH_SIGNUP_FAIL_INVALID_ROLE_TYPE);
        }
        return service;
    }

    public Boolean createAccount(AccountDto.CreateAccountReq req, String url) throws BaseException {
        return getAccountService(req.getRole()).createAccount(req, url);
    }

    public AccountDto.GetAccountRes getAccount(CustomUserDetails user) throws BaseException {
        return getAccountService(user.getRole()).getAccount(user);
    }

    public void updateAccount(CustomUserDetails user, AccountDto.UpdateAccountReq req, String url) throws BaseException {
        getAccountService(user.getRole()).updateAccount(user, req, url);
    }

    public void deactivateAccount(CustomUserDetails user) throws BaseException {
        getAccountService(user.getRole()).deactivateAccount(user);
    }

    public void requestActivation(AccountDto.RequestActivationReq req) throws BaseException {
        getAccountService(req.getRole()).requestActivation(req);
    }

    public void recoverId(AccountDto.RecoverIdReq req) throws BaseException {
        getAccountService(req.getRole()).recoverId(req);
    }

    public void recoverPassword(AccountDto.RecoverPasswordReq req) throws BaseException {
        getAccountService(req.getRole()).recoverPassword(req);
    }

    public void changePassword(CustomUserDetails user, AccountDto.ChangePasswordReq req) throws BaseException {
        getAccountService(user.getRole()).changePassword(user, req);
    }

}
