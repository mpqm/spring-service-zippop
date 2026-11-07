package com.fiiiiive.zippop.cart.model.enums;

import lombok.Getter;

@Getter
public enum Operation {

    INCREMENT("INCREMENT"),
    DECREMENT("DECREMENT");

    private final String name;

    Operation(String message) {
        this.name = message;
    }
}
