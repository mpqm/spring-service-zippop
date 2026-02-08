package com.fiiiiive.zippop.account.service;

import com.fiiiiive.zippop.account.application.EmailAuthSender;
import com.fiiiiive.zippop.account.model.AccountDto;
import com.fiiiiive.zippop.account.model.Company;
import com.fiiiiive.zippop.account.model.Customer;
import com.fiiiiive.zippop.account.policy.CustomerPolicy;
import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.global.mail.MailService;
import com.fiiiiive.zippop.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CustomerService implements AccountService {

    private final MailService mailService;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final CustomerPolicy customerPolicy;
    private final EmailAuthSender emailAuthSender;


    @Override
    @Transactional
    public Boolean createAccount(AccountDto.CreateAccountReq req, String url) throws BaseException {

        // 아이디 중복 확인
        customerPolicy.validateDuplicateUserId(req.getUserId());

        // 고객 회원(email) 조회
        Customer customer = customerRepository.findByCustomerEmail(req.getEmail()).orElse(null);
        if (customer != null) {
            customer.validateSignup();
        } else {
            customer = Customer.create(
                    req.getEmail(),
                    req.getUserId(),
                    passwordEncoder.encode(req.getPassword()),
                    req.getName(),
                    req.getPhoneNumber(),
                    req.getAddress(),
                    url
            );
            customerRepository.save(customer);
        }

        emailAuthSender.sendEmailAuth(customer);

        // 복구 회원과 신규 회원 응답 구분을 위해 isInActive 반환
        return customer.getIsInActive();

    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto.GetAccountRes getAccount(CustomUserDetails user) throws BaseException {

        // 고객 회원 조회(customerIdx)
        Customer customer = customerRepository.findByCustomerIdx(user.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.AUTH_GET_PROFILE_FAIL)
        );

        return customer.toGetInfoRes();

    }

    @Override
    @Transactional
    public void updateAccount(CustomUserDetails user, AccountDto.UpdateAccountReq req, String url) throws BaseException {

        // 없으면 dto의 기존 url 유지
        if(url == null) url = req.getProfileImageUrl();

        // 고객 회원 조회(email)
        Customer customer = customerRepository.findByCustomerIdx(user.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.AUTH_EDIT_INFO_FAIL_NOT_FOUND_MEMBER)
        );

        // 고객 회원 정보 수정
        customer.update(
                req.getName(),
                req.getAddress(),
                req.getPhoneNumber(),
                url
        );
        customerRepository.save(customer);

    }

    @Override
    @Transactional
    public void inActiveAccount(CustomUserDetails user) throws BaseException {

        // 고객 조회(email)
        Customer customer = customerRepository.findByCustomerIdx(user.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.AUTH_INACTIVE_FAIL)
        );

        // 고객 회원 이메일 인증, 비활성화 회원(isEmailAuth - 0, isInActive - 1) 여부 수정 후 저장
        customer.deactivate();
        customerRepository.save(customer);

    }

    @Override
    @Transactional
    public void activeAccount(AccountDto.UpdateAccountStatusReq req) throws BaseException {

        // 고객 회원(email) 조회
        Customer customer = customerRepository.findByCustomerEmail(req.getEmail()).orElseThrow(
                () -> new BaseException(BaseMessage.AUTH_ACTIVE_FAIL)
        );

        // if: 조회 결과가 있고, IsInactive true면 계정 복구 이메일 인증 재전송
        // else: 예외
        customer.validateActive();
        emailAuthSender.sendEmailAuth(customer);

    }

    @Override
    @Transactional
    public void activateAccount(String email) throws BaseException {

        // 고객 조회(email)
        Customer customer = customerRepository.findByCustomerEmail(email).orElseThrow(
                () -> new BaseException(BaseMessage.AUTH_VERIFY_FAIL)
        );

        // 고객 회원 이메일 인증, 비활성화 회원(isEmailAuth - 1, isInActive - 0) 여부 수정 후 저장
        customer.activate();
        customerRepository.save(customer);

    }

    @Override
    @Transactional(readOnly = true)
    public void findAccountId(AccountDto.FindAccountIdReq dto) throws BaseException {

        // 고객 회원 조회(email)
        Customer customer = customerRepository.findByCustomerEmail(dto.getEmail()).orElseThrow(
                () -> new BaseException(BaseMessage.AUTH_FIND_ID_FAIL_NOT_EXIST)
        );

        customer.validateUserIdSendable();

        mailService.sendFindUserId(
                customer.getEmail(),
                customer.getUserId(),
                customer.isInactiveButNotVerified()
        );

    }

    @Override
    @Transactional
    public void findAccountPw(AccountDto.FindAccountPwReq dto) throws BaseException {

        // 고객 회원 조회(email)
        Customer customer = customerRepository.findByUserId(dto.getUserId()).orElseThrow(
                () -> new BaseException(BaseMessage.AUTH_FIND_PW_FAIL_NOT_EXIST)
        );

        String rawPassword = customer.issueTempPassword(passwordEncoder);
        customerRepository.save(customer);

        mailService.sendFindUserPassword(
                customer.getEmail(),
                rawPassword,
                customer.isInactiveButNotVerified()
        );

    }

    @Override
    @Transactional
    public void resetAccountPw(CustomUserDetails user, AccountDto.ResetAccountPwReq req) throws BaseException {

        // 고객 회원 조회 (customerIdx)
        Customer customer = customerRepository.findByCustomerIdx(user.getIdx()).orElseThrow(
                () ->  new BaseException(BaseMessage.AUTH_RESET_PW_FAIL_NOT_FOUND_MEMBER)
        );

        customer.resetPassword(
                req.getOriginPassword(),
                req.getNewPassword(),
                passwordEncoder
        );

        customerRepository.save(customer);

    }

}
