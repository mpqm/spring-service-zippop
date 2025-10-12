package com.fiiiiive.zippop.domain.orders.dto;

import com.siot.IamportRestClient.response.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefundEvent {
    private Payment payment;
}
