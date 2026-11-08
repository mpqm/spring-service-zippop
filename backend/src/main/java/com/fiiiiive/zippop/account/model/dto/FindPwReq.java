package com.fiiiiive.zippop.account.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// PW 찾기 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindPwReq {

    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String userId;

}
