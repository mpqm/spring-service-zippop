package com.fiiiiive.zippop.account.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// ID 찾기 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindIdReq {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;

}
