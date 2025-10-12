package com.fiiiiive.zippop.domain.auth.service;

import com.fiiiiive.zippop.domain.auth.dto.AuthDto;
import com.fiiiiive.zippop.domain.auth.entity.Company;
import com.fiiiiive.zippop.domain.auth.entity.Customer;
import com.fiiiiive.zippop.domain.auth.repository.CompanyRepository;
import com.fiiiiive.zippop.domain.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.global.service.MailService;
import com.fiiiiive.zippop.global.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final MailService mailService;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;

    // 회원가입
    @Transactional
    public Boolean signup(AuthDto.SignupAuthReq dto, String url) throws BaseException {

        // 아이디 중복 확인
        if(companyRepository.findByUserId(dto.getUserId()).isPresent() || customerRepository.findByUserId(dto.getUserId()).isPresent()) {
            throw new BaseException(BaseMessage.AUTH_SIGNUP_FAIL_ALREADY_EXIST_ID);
        }

        // 시스템 역할 확인 (ROLE_CUSTOMER, ROLE_COMPANY)
        if(dto.getRole() == BaseStatus.ROLE_CUSTOMER){

            // 기업 회원으로 가입한 이메일로 고객 회원 가입 할 수 없음
            if(companyRepository.findByCompanyEmail(dto.getEmail()).isPresent()) {
                throw new BaseException(BaseMessage.AUTH_SIGNUP_FAIL_ALREADY_REGISTER_AS_COMPANY);
            }

            // 고객 회원(email) 조회
            Optional<Customer> customerOpt = customerRepository.findByCustomerEmail(dto.getEmail());
            Customer customer;

            if(customerOpt.isPresent()){
                // if: 조회 결과가 있고, IsEmailAuth true면 계정 존재 -> 예외, 그 외 계정 신규, 복구 이메일 인증 전송
                customer = customerOpt.get();
                if(customer.getIsEmailAuth()) {
                    throw new BaseException(BaseMessage.AUTH_SIGNUP_FAIL_ALREADY_EXIST);
                }
                sendVerifyEmail(customer.getEmail(), customer.getRole().name(), false, customer.getIsInActive());
            } else {
                // else: 조회 결과가 없으면 저장 / 신규 회원 이메일 인증 전송
                customer = dto.toCustomerEntity(passwordEncoder.encode(dto.getPassword()), url);
                customerRepository.save(customer);
                sendVerifyEmail(customer.getEmail(), customer.getRole().name(), customer.getIsEmailAuth(), customer.getIsInActive());
            }

            // 복구 회원과 신규 회원 응답 구분을 위해 isInActive 반환
            return customer.getIsInActive();

        } else {

            // 고객 회원으로 가입한 이메일로 기업 회원 가입 할 수 없음
            if(customerRepository.findByCustomerEmail(dto.getEmail()).isPresent()) {
                throw new BaseException(BaseMessage.AUTH_SIGNUP_FAIL_ALREADY_REGISTER_AS_CUSTOMER);
            }

            // 기업 회원(email) 조회
            Optional<Company> companyOpt = companyRepository.findByCompanyEmail(dto.getEmail());
            Company company;

            if(companyOpt.isPresent()) {
                // if: 조회 결과가 있고, IsEmailAuth true면 계정 존재 -> 예외, 그 외 계정 신규, 복구 이메일 인증 전송
                company = companyOpt.get();
                if(company.getIsEmailAuth()) {
                    throw new BaseException(BaseMessage.AUTH_SIGNUP_FAIL_ALREADY_EXIST);
                }
                sendVerifyEmail(company.getEmail(), company.getRole().name(), false, company.getIsInActive());
            } else {
                // else: 조회 결과가 없으면 저장 / 신규 회원 이메일 인증 전송
                company = dto.toCompanyEntity(passwordEncoder.encode(dto.getPassword()), url);
                companyRepository.save(company);
                sendVerifyEmail(company.getEmail(), company.getRole().name(), company.getIsEmailAuth(), company.getIsInActive());
            }

            // 복구 회원과 신규 회원 응답 구분을 위해 isInActive 반환
            return company.getIsInActive();

        }

    }

    // 이메일 인증 전송(비동기)
    @Transactional
    public void sendVerifyEmail(String email, String role, Boolean isEmailAuth, Boolean isInActive) {

        // Redis에 랜덤 UUID와 이메일 저장, 유효시간 3분으로 설정
        String uuid = redisService.saveEmailVerifyUuid(email, UUID.randomUUID().toString(), 3);

        // 회원가입 인증 이메일 전송
        mailService.sendSignupEmail(uuid, email, role, isEmailAuth, isInActive);

    }

    // 이메일 검증
    @Transactional
    public String verify(String email, String role, String inputUuid) throws BaseException {

        // 이메일에 해당하는 UUID 값 조회
        String storeUuid = redisService.getEmailVerifyUuid(email);

        // Redis에 저장된 값이 없거나 전달 받은 uuid와 다르면 이메일 인증 실패 리다이렉트 URL 반환
        if (storeUuid == null || !storeUuid.equals(inputUuid)) {
            return "http://localhost:8081/login?error=true";
        }

        // 시스템 역할 확인 (ROLE_CUSTOMER, ROLE_COMPANY)
        if(Objects.equals(role, BaseStatus.ROLE_CUSTOMER.name())){

            // 고객 조회(email)
            Customer customer = customerRepository.findByCustomerEmail(email).orElseThrow(
                    () -> new BaseException(BaseMessage.AUTH_VERIFY_FAIL)
            );

            // 고객 회원 이메일 인증, 비활성화 회원(isEmailAuth - 1, isInActive - 0) 여부 수정 후 저장
            customer.setIsEmailAuth(true);
            customer.setIsInActive(false);
            customerRepository.save(customer);

        } else {

            // 기업 조회(email)
            Company company = companyRepository.findByCompanyEmail(email).orElseThrow(
                    () -> new BaseException(BaseMessage.AUTH_VERIFY_FAIL)
            );

            // 기업 회원 이메일 인증, 비활성화 회원(isEmailAuth - 1, isInActive - 0) 여부 수정 후 저장
            company.setIsEmailAuth(true);
            company.setIsInActive(false);
            companyRepository.save(company);
        }

        // 인증 성공 후 Redis 에서 해당 이메일 관련 UUID 삭제
        redisService.deleteEmailVerifyUuid(email);

        // 성공 리다이렉트 URL 반환
        return "http://localhost:8081/login?success=true";

    }

    // 계정 비활성화
    @Transactional
    public void inactive(CustomUserDetails customUserDetails) throws BaseException {

        // 시스템 역할 확인
        if(Objects.equals(customUserDetails.getRole(), BaseStatus.ROLE_CUSTOMER.name())){

            // 고객 조회(email)
            Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseMessage.AUTH_INACTIVE_FAIL)
            );

            // 고객 회원 이메일 인증, 비활성화 회원(isEmailAuth - 0, isInActive - 1) 여부 수정 후 저장
            customer.setIsEmailAuth(false);
            customer.setIsInActive(true);
            customerRepository.save(customer);

        } else {

            // 기업 조회(email)
            Company company = companyRepository.findByCompanyIdx(customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseMessage.AUTH_INACTIVE_FAIL)
            );

            // 기업 회원 이메일 인증, 비활성화 회원(isEmailAuth - 0, isInActive - 1) 여부 수정 후 저장
            company.setIsEmailAuth(false);
            company.setIsInActive(true);
            companyRepository.save(company);

        }

    }

    // 계정 활성화
    @Transactional
    public void active(AuthDto.ActiveReq dto) throws BaseException {

        // 시스템 역할 확인 (ROLE_CUSTOMER, ROLE_COMPANY)
        if(dto.getRole() == BaseStatus.ROLE_CUSTOMER){

            // 고객 회원(email) 조회
            Customer customer = customerRepository.findByCustomerEmail(dto.getEmail()).orElseThrow(
                    () -> new BaseException(BaseMessage.AUTH_ACTIVE_FAIL)
            );

            // if: 조회 결과가 있고, IsInactive true면 계정 복구 이메일 인증 재전송
            // else: 예외
            if (customer.getIsInActive()) {
                sendVerifyEmail(customer.getEmail(), customer.getRole().name(), false, true);
            } else {
                throw new BaseException(BaseMessage.AUTH_ACTIVE_FAIL_NOT_INACTIVE);
            }

        } else {

            // 기업 회원(email) 조회
            Company company = companyRepository.findByCompanyEmail(dto.getEmail()).orElseThrow(
                    () -> new BaseException(BaseMessage.AUTH_ACTIVE_FAIL)
            );

            // if: 조회 결과가 있고, IsInactive true면 계정 복구 이메일 인증 재전송
            // else: 예외
            if(company.getIsInActive()){
                sendVerifyEmail(company.getEmail(), company.getRole().name(), false, true);
            } else {
                throw new BaseException(BaseMessage.AUTH_ACTIVE_FAIL_NOT_INACTIVE);
            }

        }

    }

    // 계정 ID 찾기
    @Transactional(readOnly = true)
    public void findId(AuthDto.FindUserIdReq dto) throws BaseException {

        // 시스템 역할 확인 (ROLE_CUSTOMER, ROLE_COMPANY)
        if(dto.getRole() == BaseStatus.ROLE_CUSTOMER) {

            // 고객 회원 조회(email)
            Customer customer = customerRepository.findByCustomerEmail(dto.getEmail()).orElseThrow(
                    () -> new BaseException(BaseMessage.AUTH_FIND_ID_FAIL_NOT_EXIST)
            );

            // if: 이메일 인증한 회원
            // else if: 비활성화 회원
            // else: 이메일 인증을 하지 않은 회원(예외)
            if (customer.getIsEmailAuth() && !customer.getIsInActive()) {
                mailService.sendFindUserId(customer.getEmail(), customer.getUserId(), false);
            } else if (!customer.getIsEmailAuth() && customer.getIsInActive()) {
                mailService.sendFindUserId(customer.getEmail(), customer.getUserId(), true);
            } else {
                throw new BaseException(BaseMessage.AUTH_FIND_ID_FAIL_NOT_EMAIL_VERIFY);
            }
        } else{

            // 기업 회원 조회(email)
            Company company = companyRepository.findByCompanyEmail(dto.getEmail()).orElseThrow(
                    () -> new BaseException(BaseMessage.AUTH_FIND_ID_FAIL_NOT_EXIST)
            );

            // if: 이메일 인증한 회원
            // else if: 비활성화 회원
            // else: 이메일 인증을 하지 않은 회원(예외)
            if (company.getIsEmailAuth() && !company.getIsInActive()) {
                mailService.sendFindUserId(company.getEmail(), company.getUserId(), false);
            } else if (!company.getIsEmailAuth() && company.getIsInActive()) {
                mailService.sendFindUserId(company.getEmail(), company.getUserId(), true);
            } else {
                throw new BaseException(BaseMessage.AUTH_FIND_ID_FAIL_NOT_EMAIL_VERIFY);
            }
        }
    }

    // 계정 PW 찾기
    @Transactional(readOnly = true)
    public void findPassword(AuthDto.FindPasswordReq dto) throws BaseException {
        // 임시 비밀 번호 생성
        String rawPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 시스템 역할 확인 (ROLE_CUSTOMER, ROLE_COMPANY)
        if(dto.getRole() == BaseStatus.ROLE_CUSTOMER) {

            // 고객 회원 조회(email)
            Optional<Customer> customerOpt = customerRepository.findByUserId(dto.getUserId());
            if(customerOpt.isPresent()) {

                // 생성된 임시 비밀번호로 변경
                Customer customer = customerOpt.get();
                customer.setPassword(encodedPassword);
                customerRepository.save(customer);

                // if: 이메일 인증한 회원
                // else if: 비활성화 회원
                // else: 이메일 인증을 하지 않은 회원(예외)
                if (!customer.getIsInActive() && customer.getIsEmailAuth()) {
                    mailService.sendFindUserPassword(customer.getEmail(), rawPassword, false);
                }
                else if (customer.getIsInActive() && !customer.getIsEmailAuth()) {
                    mailService.sendFindUserPassword(customer.getEmail(), rawPassword, true);
                }
                else {
                    throw new BaseException(BaseMessage.AUTH_FIND_PASSWORD_FAIL_NOT_EMAIL_VERIFY);
                }

            }

        } else {

            // 기업 회원 조회(email)
            Optional<Company> companyOpt = companyRepository.findByUserId(dto.getUserId());
            if(companyOpt.isPresent()){

                // 생성된 임시 비밀번호로 변경
                Company company = companyOpt.get();
                company.setPassword(encodedPassword);
                companyRepository.save(company);

                // if: 이메일 인증한 회원
                // else if: 비활성화 회원
                // else: 이메일 인증을 하지 않은 회원(예외)
                if(!company.getIsInActive() && company.getIsEmailAuth()) {
                    mailService.sendFindUserPassword(company.getEmail(), rawPassword, false);
                }
                else if (company.getIsInActive() && !company.getIsEmailAuth()) {
                    mailService.sendFindUserPassword(company.getEmail(), rawPassword, true);
                }
                else {
                    throw new BaseException(BaseMessage.AUTH_FIND_PASSWORD_FAIL_NOT_EMAIL_VERIFY);
                }

            }
        }
    }

    // 회원 정보 수정
    @Transactional
    public void editInfo(CustomUserDetails customUserDetails, AuthDto.EditInfoReq dto, String url) throws BaseException {

        if(url == null) url = dto.getProfileImageUrl();

        // 시스템 역할 확인 (ROLE_CUSTOMER, ROLE_COMPANY)
        if(Objects.equals(customUserDetails.getRole(), "ROLE_CUSTOMER")){

            // 고객 회원 조회(email)
            Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseMessage.AUTH_EDIT_INFO_FAIL_NOT_FOUND_MEMBER)
            );

            // 고객 회원 정보 수정
            customer.update(dto, url);
            customerRepository.save(customer);

        } else {

            // 기업 회원 조회(email)
            Company company = companyRepository.findByCompanyIdx(customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseMessage.AUTH_EDIT_INFO_FAIL_NOT_FOUND_MEMBER)
            );

            // 기업 회원 정보 수정
            company.update(dto, url);
            companyRepository.save(company);
        }

    }

    // 회원 비밀번호 수정
    @Transactional
    public void editPassword(CustomUserDetails customUserDetails, AuthDto.EditPasswordReq dto) throws BaseException {

        // 시스템 역할 확인 (ROLE_CUSTOMER, ROLE_COMPANY)
        if(Objects.equals(customUserDetails.getRole(), "ROLE_CUSTOMER")){

            // 고객 회원 조회 (customerIdx)
            Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                    () ->  new BaseException(BaseMessage.AUTH_EDIT_PASSWORD_FAIL_NOT_FOUND_MEMBER)
            );

            // 비밀 번호 일치 여부 검사
            if(!passwordEncoder.matches(dto.getOriginPassword(), customer.getPassword())) {
                throw new BaseException(BaseMessage.AUTH_EDIT_PASSWORD_FAIL_PASSWORD_NOT_MATCH);
            }

            // 변경된 비밀 번호로 수정
            customer.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            customerRepository.save(customer);

        } else{

            // 기업 회원 조회(companyIdx)
            Company company = companyRepository.findByCompanyIdx(customUserDetails.getIdx()).orElseThrow(
                    () ->  new BaseException(BaseMessage.AUTH_EDIT_PASSWORD_FAIL_NOT_FOUND_MEMBER)
            );

            // 비밀 번호 일치 여부 검사
            if(!passwordEncoder.matches(dto.getOriginPassword(), company.getPassword())) {
                throw new BaseException(BaseMessage.AUTH_EDIT_PASSWORD_FAIL_PASSWORD_NOT_MATCH);
            }

            // 변경된 비밀 번호로 수정
            company.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            companyRepository.save(company);

        }

    }

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public AuthDto.GetInfoRes getInfo(CustomUserDetails customUserDetails) throws BaseException {

        // 시스템 역할 확인 (ROLE_CUSTOMER, ROLE_COMPANY)
        if(Objects.equals(customUserDetails.getRole(), "ROLE_CUSTOMER")){

            // 고객 회원 조회(customerIdx)
            Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseMessage.AUTH_GET_PROFILE_FAIL)
            );

            return customer.toGetInfoRes();

        } else{

            // 기업 회원 조회(companyIdx)
            Company company = companyRepository.findByCompanyIdx(customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseMessage.AUTH_GET_PROFILE_FAIL)
            );

            return company.toGetInfoRes();
        }

    }

}
