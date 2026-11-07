package com.fiiiiive.zippop.payout.model.enums;

import lombok.Getter;

@Getter
public enum PayoutStatus {
    PAYOUT_COMPLETE("PAYOUT_COMPLETE");

    private final String name;

    PayoutStatus(String message) {
        this.name = message;
    }

}