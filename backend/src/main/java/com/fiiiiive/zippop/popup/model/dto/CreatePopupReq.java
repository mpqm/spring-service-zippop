package com.fiiiiive.zippop.popup.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 팝업 생성 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePopupReq {
    @NotBlank(message = "팝업 이름은 필수 입력 항목입니다.")
    @Size(max = 100, message = "팝업 이름은 최대 100자까지 입력 가능합니다.")
    private String popupName;

    @NotBlank(message = "팝업 주소는 필수 입력 항목입니다.")
    @Size(max = 200, message = "팝업 주소는 최대 200자까지 입력 가능합니다.")
    private String popupAddress;

    @NotBlank(message = "팝업 설명은 필수 입력 항목입니다.")
    @Size(max = 500, message = "팝업 설명은 최대 500자까지 입력 가능합니다.")
    private String popupContent;

    @NotBlank(message = "카테고리는 필수 입력 항목입니다.")
    @Size(max = 50, message = "카테고리는 최대 50자까지 입력 가능합니다.")
    private String category;

    @NotNull(message = "총 인원수는 필수 입력 항목입니다.")
    @Min(value = 1, message = "총 인원수는 최소 1명 이상이어야 합니다.")
    private Integer totalPeople;

    @NotBlank(message = "팝업 시작 날짜는 필수 입력 항목입니다.")
    private LocalDate popupStartDate;

    @NotBlank(message = "팝업 종료 날짜는 필수 입력 항목입니다.")
    private LocalDate popupEndDate;

}
