package com.fiiiiive.zippop.global.enums;

import lombok.Getter;

@Getter
public enum StoreStatus {
    STORE_START("STORE_START"),
    STORE_END("STORE_END"),
    STORE_RESERVE("STORE_RESERVE"),
    STORE_STOCK("STORE_STOCK");


    private final String name;

    StoreStatus(String message) {
        this.name = message;
    }

}
