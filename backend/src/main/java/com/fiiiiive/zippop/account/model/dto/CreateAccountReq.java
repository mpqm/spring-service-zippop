package com.fiiiiive.zippop.account.model.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

// 회원 가입 요청 DTO
@Getter
@Builder
public class CreateAccountReq {
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