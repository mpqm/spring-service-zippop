package com.fiiiiive.zippop.account.model;

public interface Account {

    String getEmail();
    String getRole();
    Boolean getIsEmailAuth();
    Boolean getIsInActive();

    default boolean isRecoveryTarget() {
        return !getIsEmailAuth() && getIsInActive();
    }
}

