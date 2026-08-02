package com.fiiiiive.zippop.account.service;

import com.fiiiiive.zippop.account.application.EmailAuthSender;
import com.fiiiiive.zippop.account.model.dto.*;
import com.fiiiiive.zippop.account.model.entity.Company;
import com.fiiiiive.zippop.account.model.dto.GetAccountRes;
import com.fiiiiive.zippop.account.policy.CompanyPolicy;
import com.fiiiiive.zippop.account.repository.CompanyRepository;
import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import com.fiiiiive.zippop.global.base.ServiceException;
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

    @Override
    @Transactional
    public Boolean createAccount(CreateAccountReq req, String url) throws ServiceException {

        // 유저 중복 확인
        companyPolicy.validateDuplicateUserId(req.getUserId());

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
            companyRepository.save(company);
        }

        // 인증 이메일 전송
        emailAuthSender.sendEmailAuth(company);

        // 복구 회원과 신규 회원 응답 구분을 위해 isInActive 반환
        return company.getIsInActive();

    }

    @Override
    @Transactional(readOnly = true)
    public GetAccountRes getAccount(CustomUserDetails user) throws ServiceException {

        // 기업 회원 조회(companyIdx)
        Company company = companyRepository.findByCompanyIdx(user.getIdx()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.AUTH_GET_PROFILE_FAIL)
        );

        return company.toDto();

    }

    @Override
    @Transactional
    public void updateAccount(CustomUserDetails user, UpdateAccountReq req, String url) throws ServiceException {

        // 없으면 dto의 기존 url 유지
        if(url == null) url = req.getProfileImageUrl();

        // 기업 회원 조회(email)
        Company company = companyRepository.findByCompanyIdx(user.getIdx()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.AUTH_EDIT_INFO_FAIL_NOT_FOUND_MEMBER)
        );

        // 기업 회원 정보 수정
        company.update(
                req.getName(),
                req.getAddress(),
                req.getCrn(),
                req.getPhoneNumber(),
                url
        );

    }

    @Override
    @Transactional
    public void deactivateAccount(CustomUserDetails user) throws ServiceException {

        // 기업 조회(email)
        Company company = companyRepository.findByCompanyIdx(user.getIdx()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.AUTH_INACTIVE_FAIL)
        );

        // 기업 회원 이메일 인증, 비활성화 회원(isEmailAuth - 0, isInActive - 1) 여부 수정 후 저장
        company.deactivate();

    }

    @Override
    @Transactional
    public void requestActivation(ActivationReq req) throws ServiceException {

        // 기업 회원(email) 조회
        Company company = companyRepository.findByCompanyEmail(req.getEmail()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.AUTH_ACTIVE_FAIL)
        );

        company.validateActive();
        emailAuthSender.sendEmailAuth(company);

    }

    @Override
    @Transactional
    public void activateAccount(String email) throws ServiceException {

        // 기업 조회(email)
        Company company = companyRepository.findByCompanyEmail(email).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.AUTH_VERIFY_FAIL)
        );

        // 기업 회원 이메일 인증, 비활성화 회원(isEmailAuth - 1, isInActive - 0) 여부 수정 후 저장
        company.activate();

    }

    @Override
    @Transactional(readOnly = true)
    public void findId(FindIdReq dto) throws ServiceException {

        // 기업 회원 조회(email)
        Company company = companyRepository.findByCompanyEmail(dto.getEmail()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.AUTH_FIND_ID_FAIL_NOT_EXIST)
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
    public void findPassword(FindPasswordReq dto) throws ServiceException {

        // 기업 회원 조회(email)
        Company company = companyRepository.findByUserId(dto.getUserId()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.AUTH_FIND_PW_FAIL_NOT_EXIST)
        );

        String rawPassword = company.issueTempPassword(passwordEncoder);

        mailService.sendFindUserPassword(
                company.getEmail(),
                rawPassword,
                company.isInactiveButNotVerified()
        );

    }

    @Override
    @Transactional
    public void resetPassword(CustomUserDetails user, ResetPasswordReq req) throws ServiceException {

        // 기업 회원 조회(companyIdx)
        Company company = companyRepository.findByCompanyIdx(user.getIdx()).orElseThrow(
                () ->  new ServiceException(ServiceErrorCode.AUTH_RESET_PW_FAIL_NOT_FOUND_MEMBER)
        );

        company.resetPassword(
                req.getOriginPassword(),
                req.getNewPassword(),
                passwordEncoder
        );

    }

}
