package com.fiiiiive.zippop.global.enums;

import lombok.Getter;

@Getter
public enum OrdersStatus {

    RESERVE_READY("RESERVE_READY"),
    RESERVE_DELIVERY("RESERVE_DELIVERY"),
    RESERVE_CANCEL("RESERVE_CANCEL"),
    RESERVE_COMPLETE("RESERVE_COMPLETE"),
    STOCK_READY("STOCK_READY"),
    STOCK_DELIVERY("STOCK_DELIVERY"),
    STOCK_CANCEL("STOCK_CANCEL"),
    STOCK_COMPLETE("STOCK_COMPLETE");

    private final String name;

    OrdersStatus(String message) {
        this.name = message;
    }
}
