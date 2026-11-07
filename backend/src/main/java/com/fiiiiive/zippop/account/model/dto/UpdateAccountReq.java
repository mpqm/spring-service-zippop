package com.fiiiiive.zippop.account.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원 정보 수정 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountReq {

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(max = 50, message = "이름은 최대 50자까지 입력 가능합니다.")
    private String name;

    @Size(max = 15, message = "사업자 등록번호는 최대 15자까지 입력 가능합니다.")
    private String crn;

    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^01[0-9]\\d{3,4}\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. 예: 01012345678")
    private String phoneNumber;

    @NotBlank(message = "주소는 필수 입력 항목입니다.")
    @Size(max = 200, message = "주소는 최대 200자까지 입력 가능합니다.")
    private String address;

    @Size(max = 2048, message = "프로필 이미지 주소가 너무 깁니다.")
    private String profileImageUrl;

}


