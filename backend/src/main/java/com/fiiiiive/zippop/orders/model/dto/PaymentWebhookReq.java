package com.fiiiiive.zippop.orders.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentWebhookReq {
    @NotBlank @JsonAlias("imp_uid") private String impUid;
    @NotBlank @JsonAlias("merchant_uid") private String merchantUid;
}
