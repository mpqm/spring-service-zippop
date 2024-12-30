package com.fiiiiive.zippop.auth.model.dto;

import com.fiiiiive.zippop.auth.model.entity.Company;
import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.global.common.constants.BaseStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jdk.jshell.Snippet;
import lombok.*;

public class AuthDto {

    // 회원 가입 요청 DTO
    @Getter
    @Builder
    public static class SignupAuthReq {
        @NotBlank(message = "역할(role)은 필수 입력 항목입니다.")
        @Pattern(regexp = "^(ROLE_CUSTOMER|ROLE_COMPANY)$", message = "역할(role)은 ROLE_CUSTOMER 또는 ROLE_COMPANY이어야 합니다.")
        private BaseStatus role;

        @NotBlank(message = "아이디는 필수 입력 항목입니다.")
        @Size(min = 5, max = 20, message = "아이디는 5자 이상, 20자 이하여야 합니다.")
        private String userId;

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        private String password;

        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        @Size(max = 50, message = "이름은 최대 50자까지 입력 가능합니다.")
        private String name;

        private String crn; // 기업 회원일 경우 사용

        @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
        @Pattern(regexp = "^(01[0-9])-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678")
        private String phoneNumber;

        @NotBlank(message = "주소는 필수 입력 항목입니다.")
        @Size(max = 200, message = "주소는 최대 200자까지 입력 가능합니다.")

        private String address;

        // Customer 엔티티로 변환
        public Customer toCustomerEntity(String encodedPassword, String profileImageUrl) {
            return Customer.builder()
                    .email(this.email)
                    .password(encodedPassword)
                    .userId(this.userId)
                    .name(this.name)
                    .role(this.role)
                    .address(this.address)
                    .phoneNumber(this.phoneNumber)
                    .point(3000) // 기본 포인트 설정
                    .isEmailAuth(false)
                    .isInActive(false)
                    .profileImageUrl(profileImageUrl)
                    .build();
        }

        // Company 엔티티로 변환
        public Company toCompanyEntity(String encodedPassword, String profileImageUrl) {
            return Company.builder()
                    .email(this.email)
                    .password(encodedPassword)
                    .userId(this.userId)
                    .name(this.name)
                    .role(this.role)
                    .address(this.address)
                    .phoneNumber(this.phoneNumber)
                    .crn(this.crn) // 기업 등록 번호
                    .isEmailAuth(false)
                    .isInActive(false)
                    .profileImageUrl(profileImageUrl)
                    .build();
        }
    }

    // 로그인 요청 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginReq {
        private String userId;
        private String password;
    }

    // 회원 정보 수정 요청 DTO
    @Getter
    @Builder
    public static class EditInfoReq {
        private String name;
        private String crn;
        private String phoneNumber;
        private String address;
        private String profileImageUrl;
    }

    // PW 수정 요청 DTO
    @Getter
    @Builder
    public static class EditPasswordReq {
        private String originPassword;
        private String newPassword;
    }

    // PW 찾기 요청 DTO
    @Getter
    @Builder
    public static class FindPasswordReq {
        String userId;
    }

    // ID 찾기 요청 DTO
    @Getter
    @Builder
    public static class FindUserIdReq {
        String email;
    }

    // 유저 정보 응답 DTO
    @Getter
    @Builder
    public static class GetInfoRes {
        private String name;
        private String email;
        private String role;
        private Integer point;
        private String phoneNumber;
        private String address;
        private String crn;
        private String profileImageUrl;
    }

}
