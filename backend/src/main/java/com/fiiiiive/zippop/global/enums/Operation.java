package com.fiiiiive.zippop.global.enums;

import lombok.Getter;

@Getter
public enum Operation {

    INCREMENT("increment"),
    DECREMENT("decrement");

    private final String name;

    Operation(String message) {
        this.name = message;
    }
}
