package com.fiiiiive.zippop.auth.service;

import com.fiiiiive.zippop.auth.model.dto.*;
import com.fiiiiive.zippop.auth.model.dto.GetInfoRes;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.auth.model.entity.Company;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.auth.repository.CompanyRepository;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.utils.MailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final MailUtil mailUtil;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    // 회원가입
    @Transactional
    public Boolean signup(PostSignupReq dto, String url) throws BaseException {
        /*
        1. 고객 및 기업 회원 가입
        if : 고객 회원(역할 확인)
            if : 기업 이메일로 고객으로 가입 할 수 없음 / isEmailAuth : true(이메일 인증 재전송), false(계정 복구 이메일 인증 전송)
            else : 조회 결과가 없으면 저장 / 신규회원 이메일 인증 전송
            return : 복구 회원과 신규회원의 구분을 위해 isInActive 반환
        else : 기업 회원
            if :  고객 이메일로 고객으로 가입 할 수 없음 / isEmailAuth : true(이메일 인증 재전송), false(계정 복구 이메일 인증 전송)
            else : 조회 결과가 없으면 저장 / 신규회원 이메일 인증 전송
            return : 복구 회원과 신규회원의 구분을 위해 isInActive 반환
         */
        if(Objects.equals(dto.getRole(), "ROLE_CUSTOMER")){
            if(companyRepository.findByCompanyEmail(dto.getEmail()).isEmpty()) {
                throw new BaseException(BaseResponseMessage.AUTH_SIGNUP_FAIL_ALREADY_REGISTER_AS_COMPANY);
            }
            Optional<Customer> customerOpt = customerRepository.findByCustomerEmail(dto.getEmail());
            Customer customer;
            if(customerOpt.isPresent()){
                customer = customerOpt.get();
                if(customer.getIsEmailAuth()) {
                    throw new BaseException(BaseResponseMessage.AUTH_SIGNUP_FAIL_ALREADY_EXIST);
                }
                sendVerifyEmail(customer.getEmail(), customer.getRole(), false, customer.getIsInActive());
            } else {
                customer = Customer.builder()
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .userId(dto.getUserId())
                        .name(dto.getName())
                        .role(dto.getRole())
                        .address(dto.getAddress())
                        .phoneNumber(dto.getPhoneNumber())
                        .point(3000)
                        .isEmailAuth(false)
                        .isInActive(false)
                        .profileImageUrl(url)
                        .build();
                customerRepository.save(customer);
                sendVerifyEmail(customer.getEmail(), customer.getRole(), customer.getIsEmailAuth(), customer.getIsInActive());
            }
            return customer.getIsInActive();
        } else {
            if(customerRepository.findByCustomerEmail(dto.getEmail()).isEmpty()) {
                throw new BaseException(BaseResponseMessage.AUTH_SIGNUP_FAIL_ALREADY_REGISTER_AS_CUSTOMER);
            }
            Optional<Company> companyOpt = companyRepository.findByCompanyEmail(dto.getEmail());
            Company company;
            if(companyOpt.isPresent()){
                company = companyOpt.get();
                if(company.getIsEmailAuth()) {
                    throw new BaseException(BaseResponseMessage.AUTH_SIGNUP_FAIL_ALREADY_EXIST);
                }
                sendVerifyEmail(company.getEmail(), company.getRole(), false, company.getIsInActive());
            } else {
                company = Company.builder()
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .userId(dto.getUserId())
                        .name(dto.getName())
                        .role(dto.getRole())
                        .address(dto.getAddress())
                        .phoneNumber(dto.getPhoneNumber())
                        .crn(dto.getCrn())
                        .isEmailAuth(false)
                        .isInActive(false)
                        .profileImageUrl(url)
                        .build();
                companyRepository.save(company);
                sendVerifyEmail(company.getEmail(), company.getRole(), company.getIsEmailAuth(), company.getIsInActive());
            }
            return company.getIsInActive();
        }
    }

    // 이메일 인증 전송(비동기 처리)
    @Transactional
    public void sendVerifyEmail(String email, String role, Boolean isEmailAuth, Boolean isInActive) {
        // 1. 렌덤 문자열 생성
        String uuid = UUID.randomUUID().toString();
        
        // 2. Redis에 랜덤 UUID와 이메일을 저장, 유효시간 3분으로 설정
        redisTemplate.opsForValue().set("emailVerify:" + email, uuid, 3, TimeUnit.MINUTES);
        
        // 3. 회원가입 인증 이메일 전송
        mailUtil.sendSignupEmail(uuid, email, role, isEmailAuth, isInActive);
    }

    // 이메일 검증
    @Transactional
    public String verify(String email, String role, String uuid) throws BaseException {
        // Redis 도입 전 : EmailVerify 존재 여부 및 UUID 일치 확인
        // EmailVerify emailVerify = emailVerifyRepository.findByEmail(email)
        //        .filter(verify -> verify.getUuid().equals(uuid))
        //        .orElseThrow(() -> new BaseException(BaseResponseMessage.MEMBER_EMAIL_VERIFY_FAIL));

        // Redis 도입 후
        // 1. 이메일에 해당하는 값 가져옴
        String redisUuid = (String) redisTemplate.opsForValue().get("emailVerify:" + email);

        // 2. Redis에 저장된 값이 없거나 전달 받은 uuid와 다르면 이메일 인증 실패 리다이렉트 URL 반환
        if (redisUuid == null || !redisUuid.equals(uuid)) {
            return "http://localhost:8081/login?error=true";
        }
        
        /*
        3. 역할(role)에 따라 고객 및 기업회원 활성화
         isEmailAuth && isInActive : 이메일 인증 전 회원(0, 0) / 이메일 인증 완료 회원(1, 0) / 비활성화 회원(0, 1)
         if : 고객 회원 이메일 인증 여부, 비활성화 여부 수정 후 저장
         else : 기업 회원 이메일 인증 여부, 비활성화 여부 수정 후 저장
         */
        if(Objects.equals(role, "ROLE_CUSTOMER")){
            Customer customer = customerRepository.findByCustomerEmail(email).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.AUTH_VERIFY_FAIL)
            );
            customer.setIsEmailAuth(true);
            customer.setIsInActive(false);
            customerRepository.save(customer);
        } else {
            Company company = companyRepository.findByCompanyEmail(email).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.AUTH_VERIFY_FAIL)
            );
            company.setIsEmailAuth(true);
            company.setIsInActive(false);
            companyRepository.save(company);
        }

        // 4. 인증 성공 후 Redis 에서 해당 이메일 관련 UUID 삭제 후 성공 리다이렉트 URL 반환
        redisTemplate.delete("emailVerify:" + email);
        return "http://localhost:8081/login?success=true";
    }

    // 계정 비활성화
    @Transactional
    public void inactive(CustomUserDetails customUserDetails) throws BaseException {
        /*
        1. 역할(role)에 따라 고객 및 기업회원 활성화
        isEmailAuth && isInActive : 이메일 인증 전 회원(0, 0) / 이메일 인증 완료 회원(1, 0) / 비활성화 회원(0, 1)
        if : 고객 회원 이메일 인증 여부, 비활성화 여부 수정 후 저장
        else : 기업 회원 이메일 인증 여부, 비활성화 여부 수정 후 저장
         */
        if(Objects.equals(customUserDetails.getRole(), "ROLE_CUSTOMER")){
            Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.AUTH_INACTIVE_FAIL)
            );
            customer.setIsEmailAuth(false);
            customer.setIsInActive(true);
            customerRepository.save(customer);
        } else {
            Company company = companyRepository.findByCompanyIdx(customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.AUTH_INACTIVE_FAIL)
            );
            company.setIsEmailAuth(false);
            company.setIsInActive(true);
            companyRepository.save(company);
        }
    }

    // 계정 ID 찾기
    public void findId(FindUserIdReq dto) throws BaseException {
        /*
        1. 고객 회원 조회 후 기업 회원 조회해 계정 ID 찾기 수행
        if : 고객 회원 확인
            if : 이메일 인증 완료한 회원
            else if: 비활성화 회원
            else : 이메일 인증을 하지 않은 회원(예외)
        else: 기업 회원 확인
            if : 이메일 인증 완료한 회원
            else if: 비활성화 회원
            else : 이메일 인증을 하지 않은 회원(예외)
         */
        Optional<Customer> customerOpt = customerRepository.findByCustomerEmail(dto.getEmail());
        if(customerOpt.isPresent()){
            Customer customer = customerOpt.get();
            if(customer.getIsEmailAuth() && !customer.getIsInActive()) {
                mailUtil.sendFindUserId(customer.getEmail(), customer.getUserId(), false);
            } else if (!customer.getIsEmailAuth() && customer.getIsInActive()){
                mailUtil.sendFindUserId(customer.getEmail(), customer.getUserId(), true);
            } else {
                throw new BaseException(BaseResponseMessage.AUTH_FIND_ID_FAIL_NOT_EMAIL_VERIFY);
            }
        } else {
            Optional<Company> companyOpt = companyRepository.findByCompanyEmail(dto.getEmail());
            if(companyOpt.isPresent()){
                Company company = companyOpt.get();
                if(company.getIsEmailAuth() && !company.getIsInActive()) {
                    mailUtil.sendFindUserId(company.getEmail(), company.getUserId(), false);
                } else if (!company.getIsEmailAuth() && company.getIsInActive()){
                    mailUtil.sendFindUserId(company.getEmail(), company.getUserId(), true);
                } else {
                    throw new BaseException(BaseResponseMessage.AUTH_FIND_ID_FAIL_NOT_EMAIL_VERIFY);
                }
            }
        }
    }

    // 계정 PW 찾기
    @Transactional
    public void findPassword(FindPasswordReq dto) throws BaseException {
        // 1. 임시 비밀 번호 생성
        String rawPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        /* 2. 고객 회원 조회 후 기업 회원 조회해 계정 PW 찾기 수행
        isEmailAuth && isInActive : 이메일 인증 전 회원(0, 0) / 이메일 인증 완료 회원(1, 0) / 비활성화 회원(0, 1)
        if : 고객 회원 확인
              if : 이메일 인증 완료한 회원
              else if: 비활성화 회원
              else : 이메일 인증을 하지 않은 회원(예외)
         else: 기업 회원 확인
              if : 이메일 인증 완료한 회원
              else if: 비활성화 회원
              else : 이메일 인증을 하지 않은 회원(예외)
        */
        Optional<Customer> customerOpt = customerRepository.findByUserId(dto.getUserId());
        if(customerOpt.isPresent()){
            Customer customer = customerOpt.get();
            customer.setPassword(encodedPassword);
            if(!customer.getIsInActive() && customer.getIsEmailAuth()) {
                mailUtil.sendFindUserPassword(customer.getEmail(), rawPassword, false);
            } else if (customer.getIsInActive() && !customer.getIsEmailAuth()){
                mailUtil.sendFindUserPassword(customer.getEmail(), rawPassword, true);
            } else {
                throw new BaseException(BaseResponseMessage.AUTH_FIND_PASSWORD_FAIL_NOT_EMAIL_VERIFY);
            }
        } else {
            Optional<Company> companyOpt = companyRepository.findByUserId(dto.getUserId());
            if(companyOpt.isPresent()){
                Company company = companyOpt.get();
                company.setPassword(encodedPassword);
                if(!company.getIsInActive() && company.getIsEmailAuth()) {
                    mailUtil.sendFindUserPassword(company.getEmail(), rawPassword, false);
                } else if (company.getIsInActive() && !company.getIsEmailAuth()){
                    mailUtil.sendFindUserPassword(company.getEmail(), rawPassword, true);
                } else {
                    throw new BaseException(BaseResponseMessage.AUTH_FIND_PASSWORD_FAIL_NOT_EMAIL_VERIFY);
                }
            }
        }
    }

    // 회원 정보 수정
    @Transactional
    public void editInfo(CustomUserDetails customUserDetails, EditInfoReq dto, String fileName) throws BaseException {
        /*
        1. 회원 정보 수정
        if: 고객 회원 : 이름. 주소, 휴대폰 번호, 프로필 이미지
        else: 기업 회원 : 이름, 주소, 사업자등록번호, 휴대폰 번호, 프로필 이미지
         */
        if(Objects.equals(customUserDetails.getRole(), "ROLE_CUSTOMER")){
            Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.AUTH_EDIT_INFO_FAIL_NOT_FOUND_MEMBER)
            );
            customer.update(
                    dto.getName(),
                    dto.getAddress(),
                    dto.getPhoneNumber(),
                    fileName
            );
            customerRepository.save(customer);
        } else {
            Company company = companyRepository.findByCompanyIdx(customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.AUTH_EDIT_INFO_FAIL_NOT_FOUND_MEMBER)
            );
            company.update(
                    dto.getName(),
                    dto.getAddress(),
                    dto.getCrn(),
                    dto.getPhoneNumber(),
                    fileName
            );
            companyRepository.save(company);
        }
    }

    // 회원 비밀번호 수정
    @Transactional
    public void editPassword(CustomUserDetails customUserDetails, EditPasswordReq dto) throws BaseException {
        // 1. 역할 확인 -> 고객 및 기업 회원 조회 -> 비밀 번호 일치 여부를 검사 -> 변경된 비밀번호 저장
        if(Objects.equals(customUserDetails.getRole(), "ROLE_CUSTOMER")){
            Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                    () ->  new BaseException(BaseResponseMessage.AUTH_EDIT_PASSWORD_FAIL_NOT_FOUND_MEMBER)
            );
            if(!passwordEncoder.matches(dto.getOriginPassword(), customer.getPassword())) {
                throw new BaseException(BaseResponseMessage.AUTH_EDIT_PASSWORD_FAIL_PASSWORD_NOT_MATCH);
            }
            customer.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            customerRepository.save(customer);
        } else{
            Company company = companyRepository.findByCompanyIdx(customUserDetails.getIdx()).orElseThrow(
                    () ->  new BaseException(BaseResponseMessage.AUTH_EDIT_PASSWORD_FAIL_NOT_FOUND_MEMBER)
            );
            if(!passwordEncoder.matches(dto.getOriginPassword(), company.getPassword())) {
                throw new BaseException(BaseResponseMessage.AUTH_EDIT_PASSWORD_FAIL_PASSWORD_NOT_MATCH);
            }
            company.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            companyRepository.save(company);
        }
    }

    // 회원 정보 조회
    public GetInfoRes getInfo(CustomUserDetails customUserDetails) throws BaseException {
        // 1. 고객 및 회원 조회 후 정보 반환
        if(Objects.equals(customUserDetails.getRole(), "ROLE_CUSTOMER")){
            Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.AUTH_GET_PROFILE_FAIL)
            );
            return GetInfoRes.builder()
                    .name(customer.getName())
                    .point(customer.getPoint())
                    .role(customer.getRole())
                    .profileImageUrl(customer.getProfileImageUrl())
                    .email(customer.getEmail())
                    .phoneNumber(customer.getPhoneNumber())
                    .address(customer.getAddress())
                    .build();
        } else{
            Company company = companyRepository.findByCompanyIdx(customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.AUTH_GET_PROFILE_FAIL)
            );
            return GetInfoRes.builder()
                    .name(company.getName())
                    .crn(company.getCrn())
                    .email(company.getEmail())
                    .role(company.getRole())
                    .phoneNumber(company.getPhoneNumber())
                    .profileImageUrl(company.getProfileImageUrl())
                    .address(company.getAddress())
                    .build();
        }
    }
}
