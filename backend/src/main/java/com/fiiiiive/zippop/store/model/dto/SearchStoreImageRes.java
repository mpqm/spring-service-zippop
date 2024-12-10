package com.fiiiiive.zippop.store.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchStoreImageRes {
    private Long storeImageIdx;
    private String storeImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
