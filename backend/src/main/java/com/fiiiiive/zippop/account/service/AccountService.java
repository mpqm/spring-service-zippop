package com.fiiiiive.zippop.account.service;

import com.fiiiiive.zippop.account.infrastructure.mail.AccountMailService;
import com.fiiiiive.zippop.account.infrastructure.redis.AccountRedisService;
import com.fiiiiive.zippop.account.model.dto.*;
import com.fiiiiive.zippop.account.model.entity.Account;
import com.fiiiiive.zippop.account.model.entity.Company;
import com.fiiiiive.zippop.account.model.entity.Customer;
import com.fiiiiive.zippop.account.model.enums.RoleType;
import com.fiiiiive.zippop.account.repository.AccountRepository;
import com.fiiiiive.zippop.account.repository.CompanyRepository;
import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountMailService accountMailService;
    private final AccountRedisService accountRedisService;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final HttpServletRequest request;

    // 이메일 인증 비동기 전송 + 인증 유효 기간 설정 메서드
    public void sendEmailAuth(Account account) {

        // Redis에 랜덤 UUID와 이메일 저장, 유효 기간 3분으로 설정
        String uuid = accountRedisService.saveEmailVerifyUuid(account.getIdx(), UUID.randomUUID().toString(), 3);

        // 회원가입 인증 이메일 전송
        String verificationUrl = ServletUriComponentsBuilder.fromContextPath(request)
                .path("/api/v1/accounts/verification")
                .queryParam("email", "{email}")
                .queryParam("uuid", "{uuid}")
                .encode()
                .buildAndExpand(Map.of("email", account.getEmail(), "uuid", uuid))
                .toUriString();
        accountMailService.sendSignupEmail(verificationUrl, account.getEmail(), account.getRole(), account.isRecoveryTarget());
    }

    @Transactional
    public CreateAccountRes createAccount(CreateAccountReq req, String image) {
        Account existingEmail = accountRepository.findByEmail(req.getEmail()).orElse(null);

        // 유저 이메일 중복 확인
        if (existingEmail != null) {
            if (!existingEmail.isRecoveryTarget() || existingEmail.getRole() != req.getRole() || !existingEmail.getUserId().equals(req.getUserId())) {
                throw new ServiceException(ServiceErrorCode.AUTH_SIGNUP_FAIL_ALREADY_EXIST);
            }
            // 이메일 인증 재전송
            sendEmailAuth(existingEmail);
            return CreateAccountRes.builder().isExistUser(true).build();
        }

        // 유저 아이디 중복 확인
        if (accountRepository.existsByUserId(req.getUserId())) {
            throw new ServiceException(ServiceErrorCode.AUTH_SIGNUP_FAIL_ALREADY_EXIST_ID);
        }

        // 유저 생성
        Account account = Account.builder()
                .userId(req.getUserId())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .name(req.getName())
                .phoneNumber(req.getPhoneNumber())
                .address(req.getAddress())
                .profileImageUrl(image)
                .isEmailAuth(false)
                .isInActive(true)
                .build();
        accountRepository.save(account);

        // 프로필 생성(분기)
        if(account.getRole() == RoleType.ROLE_CUSTOMER) {
            customerRepository.save(Customer.builder().account(account).point(3000).build());
        } else if(account.getRole() == RoleType.ROLE_COMPANY) {
            companyRepository.save(Company.builder().account(account).crn(req.getCrn()).build());
        } else {
            throw new ServiceException(ServiceErrorCode.AUTH_SIGNUP_FAIL_INVALID_ROLE_TYPE);
        }

        // 이메일 인증 전송
        sendEmailAuth(account);
        return CreateAccountRes.builder().isExistUser(false).build();
    }

    @Transactional(readOnly = true)
    public GetAccountRes getAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND));
        if(account.getRole() == RoleType.ROLE_CUSTOMER) {
            return customerRepository.findByAccountIdx(accountId)
                    .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND))
                    .toDto();
        } else if(account.getRole() == RoleType.ROLE_COMPANY) {
            return companyRepository.findByAccountIdx(accountId)
                    .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND))
                    .toDto();
        } else {
            throw new ServiceException(ServiceErrorCode.AUTH_SIGNUP_FAIL_INVALID_ROLE_TYPE);
        }
    }

    @Transactional
    public void updateAccount(Long accountId, UpdateAccountReq req, String image) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND));

        account.update(req.getName(), req.getAddress(), req.getPhoneNumber(), image);

        if (account.getRole() == RoleType.ROLE_COMPANY) {
            companyRepository.findByAccountIdx(accountId)
                    .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND))
                    .updateCrn(req.getCrn());
        }

    }

    public void activateAccount(ActivateAccountReq req) {
        Account account = accountRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND));

        if (!account.isRecoveryTarget()) {
            throw new ServiceException(ServiceErrorCode.AUTH_ACTIVE_FAIL_NOT_INACTIVE);
        }

        sendEmailAuth(account);
    }

    @Transactional
    public void deactivateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND));

        account.deactivate();
    }

    @Transactional
    public String verifyAccount(String email, String uuid) {
        Account account = accountRepository.findByEmail(email)
                .filter(Account::isRecoveryTarget)
                .orElse(null);

        boolean success = false;

        if (account != null) {
            String stored = accountRedisService.getEmailVerifyUuid(account.getIdx());

            if (stored != null && stored.equals(uuid)) {
                account.activate();
                accountRedisService.deleteEmailVerifyUuid(account.getIdx());
                success = true;
            }
        }

        return ServletUriComponentsBuilder.fromContextPath(request)
                .path("/login")
                .queryParam(success ? "success" : "error", true)
                .toUriString();
    }

    @Transactional(readOnly = true)
    public void findId(FindIdReq req) {
        Account account = accountRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND));

        accountMailService.sendFindUserId(account.getEmail(), account.getUserId(), account.isRecoveryTarget());
    }

    public void findPw(FindPwReq req) {
        Account account = accountRepository.findByUserId(req.getUserId())
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND));

        String token = java.util.UUID.randomUUID().toString();
        accountRedisService.savePasswordResetToken(token, account.getIdx(), 10);

        String resetUrl = ServletUriComponentsBuilder.fromContextPath(request)
                .path("/password-reset")
                .queryParam("token", "{token}")
                .encode()
                .buildAndExpand(Map.of("token", token))
                .toUriString();
        accountMailService.sendPasswordResetLink(account.getEmail(), resetUrl);
    }

    @Transactional
    public void resetPw(ResetPwReq req) {
        Long accountId = accountRedisService.getPasswordResetAccountId(req.getToken());
        if (accountId == null) {
            throw new ServiceException(ServiceErrorCode.AUTH_RESET_PW_FAIL);
        }
        accountRepository.findById(accountId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND))
                .resetPw(req.getNewPassword(), passwordEncoder);
        accountRepository.flush();

        if (!accountId.equals(accountRedisService.consumePasswordResetToken(req.getToken()))) {
            throw new ServiceException(ServiceErrorCode.AUTH_RESET_PW_FAIL);
        }
    }

    @Transactional
    public void replacePw(CustomUserDetails user, ReplacePwReq req) {
        accountRepository.findById(user.getAccountId())
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND))
                .replacePw(req.getOriginPassword(), req.getNewPassword(), passwordEncoder);
    }

}
