package com.fiiiiive.zippop.account.service;

import com.fiiiiive.zippop.account.application.EmailAuthSender;
import com.fiiiiive.zippop.account.model.AccountDto;
import com.fiiiiive.zippop.account.model.Company;
import com.fiiiiive.zippop.account.policy.CompanyPolicy;
import com.fiiiiive.zippop.account.repository.CompanyRepository;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.enums.RoleType;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.global.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyService implements AccountService {

    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final EmailAuthSender emailAuthSender;
    private final CompanyPolicy companyPolicy;

    /**
     * 기업 회원 가입
     * DDD: Application Service - 여러 도메인 객체와 정책을 조율
     */
    @Override
    @Transactional
    public Boolean createAccount(AccountDto.CreateAccountReq req, String url) throws BaseException {

        // DDD: Policy를 통한 도메인 규칙 검증
        companyPolicy.validateDuplicateUserId(req.getUserId());
        
        // Value Object 변환 및 검증
        companyPolicy.validateRoleType(RoleType.fromString(req.getRole()));

        // 기업 회원(email) 조회
        Company company = companyRepository.findByCompanyEmail(req.getEmail()).orElse(null);
        if (company != null) {
            company.validateSignup();
        } else {
            company = Company.create(
                    req.getEmail(),
                    req.getUserId(),
                    passwordEncoder.encode(req.getPassword()),
                    req.getName(),
                    req.getCrn(),
                    req.getPhoneNumber(),
                    req.getAddress(),
                    url
            );
            company.validateRole();
            companyRepository.save(company);
        }

        emailAuthSender.sendEmailAuth(company);

        // 복구 회원과 신규 회원 응답 구분을 위해 isInActive 반환
        return company.getIsInActive();

    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto.GetAccountRes getAccount(CustomUserDetails user) throws BaseException {

        // 기업 회원 조회(companyIdx)
        Company company = companyRepository.findByCompanyIdx(user.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.AUTH_GET_PROFILE_FAIL)
        );

        return company.toDto();

    }

    @Override
    @Transactional
    public void updateAccount(CustomUserDetails user, AccountDto.UpdateAccountReq req, String url) throws BaseException {

        // 없으면 dto의 기존 url 유지
        if(url == null) url = req.getProfileImageUrl();

        // 기업 회원 조회(email)
        Company company = companyRepository.findByCompanyIdx(user.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.AUTH_EDIT_INFO_FAIL_NOT_FOUND_MEMBER)
        );

        // 기업 회원 정보 수정
        company.update(
                req.getName(),
                req.getAddress(),
                req.getCrn(),
                req.getPhoneNumber(),
                url
        );
        companyRepository.save(company);

    }

    @Override
    @Transactional
    public void deactivateAccount(CustomUserDetails user) throws BaseException {

        // 기업 조회(email)
        Company company = companyRepository.findByCompanyIdx(user.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.AUTH_INACTIVE_FAIL)
        );

        // 기업 회원 이메일 인증, 비활성화 회원(isEmailAuth - 0, isInActive - 1) 여부 수정 후 저장
        company.deactivate();
        companyRepository.save(company);

    }

    @Override
    @Transactional
    public void requestActivation(AccountDto.UpdateAccountStatusReq req) throws BaseException {

        // 기업 회원(email) 조회
        Company company = companyRepository.findByCompanyEmail(req.getEmail()).orElseThrow(
                () -> new BaseException(BaseMessage.AUTH_ACTIVE_FAIL)
        );

        company.validateActive();
        emailAuthSender.sendEmailAuth(company);

    }

    @Override
    @Transactional
    public void activateAccount(String email) throws BaseException {

        // 기업 조회(email)
        Company company = companyRepository.findByCompanyEmail(email).orElseThrow(
                () -> new BaseException(BaseMessage.AUTH_VERIFY_FAIL)
        );

        // 기업 회원 이메일 인증, 비활성화 회원(isEmailAuth - 1, isInActive - 0) 여부 수정 후 저장
        company.activate();
        companyRepository.save(company);

    }

    @Override
    @Transactional(readOnly = true)
    public void recoverUsername(AccountDto.FindAccountIdReq dto) throws BaseException {

        // 기업 회원 조회(email)
        Company company = companyRepository.findByCompanyEmail(dto.getEmail()).orElseThrow(
                () -> new BaseException(BaseMessage.AUTH_FIND_ID_FAIL_NOT_EXIST)
        );

        company.validateUserIdSendable();

        mailService.sendFindUserId(
                company.getEmail(),
                company.getUserId(),
                company.isInactiveButNotVerified()
        );

    }

    @Override
    @Transactional
    public void recoverPassword(AccountDto.FindAccountPwReq dto) throws BaseException {

        // 기업 회원 조회(email)
        Company company = companyRepository.findByUserId(dto.getUserId()).orElseThrow(
                () -> new BaseException(BaseMessage.AUTH_FIND_PW_FAIL_NOT_EXIST)
        );

        String rawPassword = company.issueTempPassword(passwordEncoder);
        companyRepository.save(company);

        mailService.sendFindUserPassword(
                company.getEmail(),
                rawPassword,
                company.isInactiveButNotVerified()
        );

    }

    @Override
    @Transactional
    public void changePassword(CustomUserDetails user, AccountDto.ResetAccountPwReq req) throws BaseException {

        // 기업 회원 조회(companyIdx)
        Company company = companyRepository.findByCompanyIdx(user.getIdx()).orElseThrow(
                () ->  new BaseException(BaseMessage.AUTH_RESET_PW_FAIL_NOT_FOUND_MEMBER)
        );

        company.resetPassword(
                req.getOriginPassword(),
                req.getNewPassword(),
                passwordEncoder
        );
        
        companyRepository.save(company);

    }

}
