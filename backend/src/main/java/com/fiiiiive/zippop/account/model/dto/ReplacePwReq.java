package com.fiiiiive.zippop.account.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// PW 수정 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplacePwReq {

    @NotBlank(message = "이전 패스워드는 필수 입력 항목입니다.")
    private String originPassword;

    @NotBlank(message = "새로운 패스워드는 필수 입력 항목입니다.")
    @Size(min = 8, max = 72, message = "새로운 패스워드는 8자 이상, 72자 이하여야 합니다.")
    private String newPassword;

}
