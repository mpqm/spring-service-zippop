package com.fiiiiive.zippop.auth.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindPasswordReq {
    String userId;
}
