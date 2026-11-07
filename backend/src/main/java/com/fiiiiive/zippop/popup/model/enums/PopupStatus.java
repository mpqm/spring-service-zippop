package com.fiiiiive.zippop.popup.model.enums;

import lombok.Getter;

@Getter
public enum PopupStatus {
    POPUP_START("POPUP_START"),
    POPUP_END("POPUP_END"),
    POPUP_RESERVE("POPUP_RESERVE"),
    POPUP_STOCK("POPUP_STOCK");


    private final String name;

    PopupStatus(String message) {
        this.name = message;
    }

}
