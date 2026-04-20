package com.fiiiiive.zippop.popup.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 팝업 이미지 조회 응답 DTO
@Getter
@Builder
public class GetPopupImageRes {
    private Long popupImageIdx;
    private String popupImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}