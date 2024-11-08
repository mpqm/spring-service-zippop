package com.fiiiiive.zippop.auth.service;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.auth.model.entity.Company;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.auth.model.dto.EditInfoReq;
import com.fiiiiive.zippop.auth.model.dto.EditPasswordReq;
import com.fiiiiive.zippop.auth.model.dto.PostSignupReq;
import com.fiiiiive.zippop.auth.model.dto.SearchProfileRes;
import com.fiiiiive.zippop.auth.model.dto.PostSignupRes;
import com.fiiiiive.zippop.auth.model.entity.EmailVerify;
import com.fiiiiive.zippop.auth.repository.CompanyRepository;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.auth.repository.EmailVerifyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final JavaMailSender emailSender;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final EmailVerifyRepository emailVerifyRepository;
    private final PasswordEncoder passwordEncoder;

    public PostSignupRes signup(PostSignupReq dto) throws BaseException {
        if(dto.getCrn() != null && Objects.equals(dto.getRole(), "ROLE_COMPANY")){
            if(customerRepository.findByCustomerEmail(dto.getEmail()).isPresent()) {
                throw new BaseException(BaseResponseMessage.MEMBER_REGISTER_FAIL_ALREADY_REGISTER_AS_CUSTOMER);
            }
            Optional<Company> result = companyRepository.findByCompanyEmail(dto.getEmail());
            if(result.isPresent()){
                Company company = result.get();
                if(!company.getIsEmailAuth() && company.getIsInActive()) {
                    return PostSignupRes.builder()
                            .idx(company.getIdx())
                            .email(company.getEmail())
                            .isEmailAuth(company.getIsEmailAuth())
                            .isInactive(company.getIsInActive())
                            .role(company.getRole())
                            .build();
                } else {
                    throw new BaseException(BaseResponseMessage.MEMBER_REGISTER_FAIL_ALREADY_EXIST);
                }
            } else{
                Company company = Company.builder()
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .name(dto.getName())
                        .crn(dto.getCrn())
                        .role(dto.getRole())
                        .address(dto.getAddress())
                        .phoneNumber(dto.getPhoneNumber())
                        .isEmailAuth(false)
                        .isInActive(false)
                        .build();
                companyRepository.save(company);
                return PostSignupRes.builder()
                        .idx(company.getIdx())
                        .role(dto.getRole())
                        .isEmailAuth(company.getIsEmailAuth())
                        .isInactive(company.getIsInActive())
                        .email(dto.getEmail())
                        .build();
            }
        } else {
            if(companyRepository.findByCompanyEmail(dto.getEmail()).isPresent()) {
                throw new BaseException(BaseResponseMessage.MEMBER_REGISTER_FAIL_ALREADY_REGISTER_AS_COMPANY);
            }
            Optional<Customer> result = customerRepository.findByCustomerEmail(dto.getEmail());
            if(result.isPresent()){
                Customer customer = result.get();
                if(!customer.getIsEmailAuth() && customer.getIsInActive()){
                    return PostSignupRes.builder()
                            .idx(customer.getIdx())
                            .role(customer.getRole())
                            .isEmailAuth(customer.getIsEmailAuth())
                            .isInactive(customer.getIsInActive())
                            .email(customer.getEmail())
                            .build();
                }
                else {
                    throw new BaseException(BaseResponseMessage.MEMBER_REGISTER_FAIL_ALREADY_EXIST);
                }
            } else {
                Customer customer = Customer.builder()
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .name(dto.getName())
                        .role(dto.getRole())
                        .address(dto.getAddress())
                        .phoneNumber(dto.getPhoneNumber())
                        .point(3000)
                        .isEmailAuth(false)
                        .isInActive(false)
                        .build();
                customerRepository.save(customer);
                return PostSignupRes.builder()
                        .idx(customer.getIdx())
                        .role(customer.getRole())
                        .isEmailAuth(customer.getIsEmailAuth())
                        .isInactive(customer.getIsInActive())
                        .email(customer.getEmail())
                        .build();
            }
        }
    }

    public void activeMember(String email, String role) throws BaseException {
        if(Objects.equals(role, "ROLE_COMPANY")){
            Optional<Company> result = companyRepository.findByCompanyEmail(email);
            if(result.isPresent()){
                Company company = result.get();
                company.setIsEmailAuth(true);
                company.setIsInActive(false);
                companyRepository.save(company);
            } else {
                throw new BaseException(BaseResponseMessage.MEMBER_EMAIL_VERIFY_FAIL);
            }
        } else {
            Optional<Customer> result = customerRepository.findByCustomerEmail(email);
            if(result.isPresent()) {
                Customer customer = result.get();
                customer.setIsEmailAuth(true);
                customer.setIsInActive(false);
                customerRepository.save(customer);
            } else {
                throw new BaseException(BaseResponseMessage.MEMBER_EMAIL_VERIFY_FAIL);
            }
        }
    }

    public String sendEmail(PostSignupRes response) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(response.getEmail());
        if(Objects.equals(response.getRole(), "ROLE_COMPANY")){
            if(!response.getIsEmailAuth() && !response.getIsInactive()){
                message.setSubject("ZIPPOP - 기업으로 가입하신걸 환영합니다.");
            } else {
                message.setSubject("ZIPPOP - 기업회원계정 복구 이메일");
            }
        }
        else {
            if(!response.getIsEmailAuth() && !response.getIsInactive()){
                message.setSubject("ZIPPOP - 고객으로 가입하신걸 환영합니다.");
            } else {
                message.setSubject("ZIPPOP - 고객회원계정 복구 이메일");
            }
        }
        String uuid = UUID.randomUUID().toString();
        message.setText("http://localhost:8080/api/v1/member/verify?email="+response.getEmail()+"&role="+response.getRole()+"&uuid="+uuid);
        emailSender.send(message);
        return uuid;
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
                emailVerifyRepository.deleteByEmail(email);
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
                    emailVerifyRepository.deleteByEmail(email);
                }
            } else {
                throw new BaseException(BaseResponseMessage.MEMBER_INACTIVE_FAIL);
            }
        }
    }

    public Boolean isExist(String email, String uuid) throws BaseException {
        Optional<EmailVerify> result = emailVerifyRepository.findByEmail(email);
        if (result.isPresent()) {
            EmailVerify emailVerify = result.get();
            return emailVerify.getUuid().equals(uuid);
        } else { throw new BaseException(BaseResponseMessage.MEMBER_EMAIL_VERIFY_FAIL); }
    }

    @Transactional
    public void save(String email, String uuid) throws BaseException{
        EmailVerify emailVerify = EmailVerify.builder()
                .email(email)
                .uuid(uuid)
                .build();
        emailVerifyRepository.save(emailVerify);
    }

    @Transactional
    public void editInfo(CustomUserDetails customUserDetails, EditInfoReq dto) throws BaseException {
        if(Objects.equals(customUserDetails.getRole(), "ROLE_COMPANY")){
            Optional<Company> result = companyRepository.findByCompanyIdx(customUserDetails.getIdx());
            if(result.isPresent()){
                Company company = result.get();
                company.setName(dto.getName());
                company.setAddress(dto.getAddress());
                company.setCrn(dto.getCrn());
                company.setPhoneNumber(dto.getPhoneNumber());
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

    public SearchProfileRes getProfile(CustomUserDetails customUserDetails) throws BaseException {
        if(Objects.equals(customUserDetails.getRole(), "ROLE_CUSTOMER")){
            Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx())
            .orElseThrow(() -> new BaseException(BaseResponseMessage.MEMBER_PROFILE_FAIL));
            return SearchProfileRes.builder()
                    .name(customer.getName())
                    .point(customer.getPoint())
                    .email(customer.getEmail())
                    .phoneNumber(customer.getPhoneNumber())
                    .address(customer.getAddress())
                    .build();
        } else{
            Company company = companyRepository.findByCompanyIdx(customUserDetails.getIdx())
            .orElseThrow(() -> new BaseException(BaseResponseMessage.MEMBER_PROFILE_FAIL));
            return SearchProfileRes.builder()
                    .name(company.getName())
                    .crn(company.getCrn())
                    .email(company.getEmail())
                    .phoneNumber(company.getPhoneNumber())
                    .address(company.getAddress())
                    .build();
        }
    }
}
