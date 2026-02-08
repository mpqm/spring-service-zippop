package com.fiiiiive.zippop.account.model;

import com.fiiiiive.zippop.global.enums.RoleType;

// Account 도메인 인터페이스
public interface Account {

    String getEmail();
    RoleType getRole();
    Boolean getIsEmailAuth();
    Boolean getIsInActive();
    default boolean isRecoveryTarget() {
        return !getIsEmailAuth() && getIsInActive();
    }
    void validateRole();

}

