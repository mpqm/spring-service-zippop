package com.fiiiiive.zippop.account.model;

import jakarta.validation.constraints.*;
import lombok.*;

public class AccountDto {

    // 로그인 요청 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginReq {
        private String userId;
        private String password;
    }

    // 회원 가입 요청 DTO
    @Getter
    @Builder
    public static class CreateAccountReq {
        @NotNull(message = "역할은 필수 입력 항목입니다.")
        private String role;

        @NotBlank(message = "아이디는 필수 입력 항목입니다.")
        @Size(min = 5, max = 20, message = "아이디는 5자 이상, 20자 이하여야 합니다.")
        private String userId;

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Size(min = 4, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        private String password;

        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        @Size(max = 50, message = "이름은 최대 50자까지 입력 가능합니다.")
        private String name;

        @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
        @Pattern(regexp = "^01[0-9]\\d{3,4}\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. 예: 01012345678")
        private String phoneNumber;

        @NotBlank(message = "주소는 필수 입력 항목입니다.")
        @Size(max = 200, message = "주소는 최대 200자까지 입력 가능합니다.")
        private String address;

        private String crn; // 기업 회원일 경우 사용

    }

    // 유저 정보 응답 DTO
    @Getter
    @Builder
    public static class GetAccountRes {
        private String name;
        private String email;
        private String role;
        private Integer point;
        private String phoneNumber;
        private String address;
        private String crn;
        private String profileImageUrl;
    }

    // 회원 정보 수정 요청 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateAccountReq {
        private String name;
        private String crn;
        private String phoneNumber;
        private String address;
        private String profileImageUrl;
    }

    // 계정 활성화 요청 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestActivationReq {

        @NotNull(message = "역할은 필수 입력 항목입니다.")
        private String role;

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        private String email;

    }

    // PW 찾기 요청 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindPasswordReq {

        @NotNull(message = "역할은 필수 입력 항목입니다.")
        private String role;

        @NotBlank(message = "아이디는 필수 입력 항목입니다.")
        private String userId;

    }

    // ID 찾기 요청 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindIdReq {

        @NotNull(message = "역할은 필수 입력 항목입니다.")
        private String role;

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        private String email;

    }

    // PW 수정 요청 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetPasswordReq {

        @NotNull(message = "이전 패스워드는 필수 입력 항목입니다.")
        private String originPassword;

        @NotNull(message = "새로운 패스워드는 필수 입력 항목입니다.")
        private String newPassword;

    }

}
