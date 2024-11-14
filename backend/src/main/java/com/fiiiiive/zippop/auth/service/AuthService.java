package com.fiiiiive.zippop.auth.service;

import com.fiiiiive.zippop.auth.model.dto.*;
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

    @Transactional
    public PostSignupRes signup(PostSignupReq dto, String url) throws BaseException {
        if(dto.getCrn() != null && Objects.equals(dto.getRole(), "ROLE_COMPANY")){
            if(customerRepository.findByCustomerEmail(dto.getEmail()).isPresent()) {
                throw new BaseException(BaseResponseMessage.MEMBER_REGISTER_FAIL_ALREADY_REGISTER_AS_CUSTOMER);
            }
            Optional<Company> result = companyRepository.findByCompanyEmail(dto.getEmail());
            if(result.isPresent()){
                Company company = result.get();
                if(!company.getIsEmailAuth()) {
                    PostSignupRes postSignupRes = PostSignupRes.builder()
                            .idx(company.getIdx())
                            .userId(company.getUserId())
                            .email(company.getEmail())
                            .isEmailAuth(company.getIsEmailAuth())
                            .isInactive(company.getIsInActive())
                            .role(company.getRole())
                            .build();
                    verifyEmail(postSignupRes);
                    return postSignupRes;
                } else {
                    throw new BaseException(BaseResponseMessage.MEMBER_REGISTER_FAIL_ALREADY_EXIST);
                }
            } else{
                Company company = Company.builder()
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .name(dto.getName())
                        .userId(dto.getUserId())
                        .crn(dto.getCrn())
                        .role(dto.getRole())
                        .address(dto.getAddress())
                        .phoneNumber(dto.getPhoneNumber())
                        .isEmailAuth(false)
                        .isInActive(false)
                        .profileImageUrl(url)
                        .build();
                companyRepository.save(company);
                PostSignupRes postSignupRes = PostSignupRes.builder()
                        .idx(company.getIdx())
                        .userId(dto.getUserId())
                        .role(dto.getRole())
                        .isEmailAuth(company.getIsEmailAuth())
                        .isInactive(company.getIsInActive())
                        .email(dto.getEmail())
                        .build();
                verifyEmail(postSignupRes);
                return postSignupRes;
            }
        } else {
            if(companyRepository.findByCompanyEmail(dto.getEmail()).isPresent()) {
                throw new BaseException(BaseResponseMessage.MEMBER_REGISTER_FAIL_ALREADY_REGISTER_AS_COMPANY);
            }
            Optional<Customer> result = customerRepository.findByCustomerEmail(dto.getEmail());
            if(result.isPresent()){
                Customer customer = result.get();
                if(!customer.getIsEmailAuth()) {
                    PostSignupRes postSignupRes = PostSignupRes.builder()
                            .idx(customer.getIdx())
                            .role(customer.getRole())
                            .userId(customer.getUserId())
                            .isEmailAuth(customer.getIsEmailAuth())
                            .isInactive(customer.getIsInActive())
                            .email(customer.getEmail())
                            .build();
                    verifyEmail(postSignupRes);
                    return postSignupRes;
                }
                else {
                    throw new BaseException(BaseResponseMessage.MEMBER_REGISTER_FAIL_ALREADY_EXIST);
                }
            } else {
                Customer customer = Customer.builder()
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
                PostSignupRes postSignupRes = PostSignupRes.builder()
                        .idx(customer.getIdx())
                        .role(customer.getRole())
                        .isEmailAuth(customer.getIsEmailAuth())
                        .isInactive(customer.getIsInActive())
                        .email(customer.getEmail())
                        .build();
                verifyEmail(postSignupRes);
                return postSignupRes;
            }
        }
    }

    @Transactional
    public void verifyEmail(PostSignupRes dto) {
        // Redis에 UUID와 이메일을 저장하고 TTL(Time-To-Live)을 설정 (예: 10분)
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("emailVerify:" + dto.getEmail(), uuid, 3, TimeUnit.MINUTES);
        mailUtil.sendSignupEmail(uuid, dto);
    };

    @Transactional
    public Boolean activeMember(String email, String role, String uuid) throws BaseException {
        // EmailVerify 존재 여부 및 UUID 일치 확인
        // EmailVerify emailVerify = emailVerifyRepository.findByEmail(email)
        //        .filter(verify -> verify.getUuid().equals(uuid))
        //        .orElseThrow(() -> new BaseException(BaseResponseMessage.MEMBER_EMAIL_VERIFY_FAIL));
        String redisUuid = (String) redisTemplate.opsForValue().get("emailVerify:" + email);
        if (redisUuid == null || !redisUuid.equals(uuid)) {
            return false;
        }
        // 역할(role)에 따라 Company 또는 Customer 활성화
        if (Objects.equals(role, "ROLE_COMPANY")) {
            Company company = companyRepository.findByCompanyEmail(email)
                    .orElseThrow(() -> new BaseException(BaseResponseMessage.MEMBER_EMAIL_VERIFY_FAIL));
            company.setIsEmailAuth(true);
            company.setIsInActive(false);
            companyRepository.save(company);
        } else {
            Customer customer = customerRepository.findByCustomerEmail(email)
            .orElseThrow(() -> new BaseException(BaseResponseMessage.MEMBER_EMAIL_VERIFY_FAIL));
            customer.setIsEmailAuth(true);
            customer.setIsInActive(false);
            customerRepository.save(customer);
        }
        // 인증 성공 후 Redis에서 해당 이메일 관련 UUID 삭제
        redisTemplate.delete("emailVerify:" + email);
        return true;
    }

    @Transactional
    public void inActiveMember(CustomUserDetails customUserDetails) throws BaseException {
        String email = customUserDetails.getUsername();
        String role = customUserDetails.getRole();
        Long idx = customUserDetails.getIdx();
        if(Objects.equals(role, "ROLE_COMPANY")){
            Optional<Company> result = companyRepository.findByCompanyEmail(email);
            if(result.isPresent()){
                Company company = result.get();
                company.setIsEmailAuth(false);
                company.setIsInActive(true);
                companyRepository.save(company);
            } else {
                throw new BaseException(BaseResponseMessage.MEMBER_INACTIVE_FAIL);
            }
        } else {
            Optional<Customer> result = customerRepository.findByCustomerEmail(customUserDetails.getEmail());
            if(result.isPresent()) {
                Customer customer = result.get();
                customer.setIsEmailAuth(false);
                customer.setIsInActive(true);
                if(customer.getPassword() == null){
                    customerRepository.save(customer);
                } else {
                    customerRepository.save(customer);
                }
            } else {
                throw new BaseException(BaseResponseMessage.MEMBER_INACTIVE_FAIL);
            }
        }
    }

    public void findId(FindUserIdReq dto) throws BaseException {
        Optional<Customer> resultCustomer = customerRepository.findByCustomerEmail(dto.getEmail());
        if(resultCustomer.isPresent()){
            Customer customer = resultCustomer.get();
            if(!customer.getIsInActive() && customer.getIsEmailAuth()) {
                mailUtil.sendFindUserId(customer.getEmail(), customer.getUserId(), false);
            } else if (customer.getIsInActive() && !customer.getIsEmailAuth()){
                mailUtil.sendFindUserId(customer.getEmail(), customer.getUserId(), true);
            } else {
                throw new BaseException(BaseResponseMessage.MEMBER_FIND_ID_FAIL_EMAIL_VERIFY);
            }
        }
        Optional<Company> resultCompany = companyRepository.findByCompanyEmail(dto.getEmail());
        if(resultCompany.isPresent()){
            Company company = resultCompany.get();
            if(!company.getIsInActive() && company.getIsEmailAuth()) {
                mailUtil.sendFindUserId(company.getEmail(), company.getUserId(), false);
            } else if (company.getIsInActive() && !company.getIsEmailAuth()){
                mailUtil.sendFindUserId(company.getEmail(), company.getUserId(), true);
            } else {
                throw new BaseException(BaseResponseMessage.MEMBER_FIND_ID_FAIL_EMAIL_VERIFY);
            }
        }
    }

    @Transactional
    public void findPassword(FindPasswordReq dto) throws BaseException {
        Optional<Customer> resultCustomer = customerRepository.findByUserId(dto.getUserId());
        if(resultCustomer.isPresent()){
            Customer customer = resultCustomer.get();
            String uuid = UUID.randomUUID().toString();
            String rawPassword = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            customer.setPassword(encodedPassword);
            if(!customer.getIsInActive() && customer.getIsEmailAuth()) {
                mailUtil.sendFindUserPassword(customer.getEmail(), uuid, false);
            } else if (customer.getIsInActive() && !customer.getIsEmailAuth()){
                mailUtil.sendFindUserPassword(customer.getEmail(), uuid, true);
            } else {
                throw new BaseException(BaseResponseMessage.MEMBER_FIND_PASSWORD_FAIL_EMAIL_VERIFY);
            }
        }
        Optional<Company> resultCompany = companyRepository.findByUserId(dto.getUserId());
        if(resultCompany.isPresent()){
            Company company = resultCompany.get();
            String rawPassword = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            company.setPassword(encodedPassword);
            if(!company.getIsInActive() && company.getIsEmailAuth()) {
                mailUtil.sendFindUserPassword(company.getEmail(), rawPassword, false);
            } else if (company.getIsInActive() && !company.getIsEmailAuth()){
                mailUtil.sendFindUserPassword(company.getEmail(), rawPassword, true);
            } else {
                throw new BaseException(BaseResponseMessage.MEMBER_FIND_PASSWORD_FAIL_EMAIL_VERIFY);
            }
        }
    }

    @Transactional
    public void editInfo(CustomUserDetails customUserDetails, EditInfoReq dto, String fileName) throws BaseException {
        if(Objects.equals(customUserDetails.getRole(), "ROLE_COMPANY")){
            Optional<Company> result = companyRepository.findByCompanyIdx(customUserDetails.getIdx());
            if(result.isPresent()){
                Company company = result.get();
                company.setName(dto.getName());
                company.setAddress(dto.getAddress());
                company.setCrn(dto.getCrn());
                company.setPhoneNumber(dto.getPhoneNumber());
                company.setProfileImageUrl(fileName);
                companyRepository.save(company);
            } else {
                throw new BaseException(BaseResponseMessage.MEMBER_EDIT_INFO_FAIL);
            }
        } else {
            Optional<Customer> result = customerRepository.findByCustomerIdx(customUserDetails.getIdx());
            if(result.isPresent()) {
                Customer customer = result.get();
                customer.setName(dto.getName());
                customer.setAddress(dto.getAddress());
                customer.setPhoneNumber(dto.getPhoneNumber());
                customer.setProfileImageUrl(fileName);
                customerRepository.save(customer);
            } else {
                throw new BaseException(BaseResponseMessage.MEMBER_EDIT_INFO_FAIL);
            }
        }
    }

    @Transactional
    public void editPassword(CustomUserDetails customUserDetails, EditPasswordReq dto) throws BaseException {
        if(Objects.equals(customUserDetails.getRole(), "ROLE_COMPANY")){
            Optional<Company> result = companyRepository.findByCompanyIdx(customUserDetails.getIdx());
            if(result.isPresent()){
                Company company = result.get();
                if(passwordEncoder.matches(dto.getOriginPassword(), company.getPassword()))
                {
                    company.setPassword(passwordEncoder.encode(dto.getNewPassword()));
                    companyRepository.save(company);
                }
                else {
                    throw new BaseException(BaseResponseMessage.MEMBER_EDIT_PASSWORD_FAIL_PASSWORD_NOT_MATCH);
                }
            } else {
                throw new BaseException(BaseResponseMessage.MEMBER_EDIT_PASSWORD_FAIL);
            }
        } else {
            Optional<Customer> result = customerRepository.findByCustomerIdx(customUserDetails.getIdx());
            if(result.isPresent()) {
                Customer customer = result.get();
                if(passwordEncoder.matches(dto.getOriginPassword(), customer.getPassword()))
                {
                    customer.setPassword(passwordEncoder.encode(dto.getNewPassword()));
                    customerRepository.save(customer);
                }
                else {
                    throw new BaseException(BaseResponseMessage.MEMBER_EDIT_PASSWORD_FAIL_PASSWORD_NOT_MATCH);
                }
            } else {
                throw new BaseException(BaseResponseMessage.MEMBER_EDIT_PASSWORD_FAIL);
            }
        }
    }

    public GetInfoRes getInfo(CustomUserDetails customUserDetails) throws BaseException {
        if(Objects.equals(customUserDetails.getRole(), "ROLE_CUSTOMER")){
            Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx())
            .orElseThrow(() -> new BaseException(BaseResponseMessage.MEMBER_PROFILE_FAIL));
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
            Company company = companyRepository.findByCompanyIdx(customUserDetails.getIdx())
            .orElseThrow(() -> new BaseException(BaseResponseMessage.MEMBER_PROFILE_FAIL));
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
