package com.fiiiiive.zippop.global.enums;

import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import lombok.Getter;

// 역할 타입 Enum
@Getter
public enum RoleType {

        ROLE_CUSTOMER("ROLE_CUSTOMER", "고객 회원"),
        ROLE_COMPANY("ROLE_COMPANY", "기업 회원");

        private final String name;
        private final String description;

        RoleType(String name, String description) {
                this.name = name;
                this.description = description;
        }

}
