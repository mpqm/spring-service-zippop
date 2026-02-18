package com.fiiiiive.zippop.global.enums;

import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import lombok.Getter;

@Getter
public enum GoodsStatus {
    GOODS_STOCK("GOODS_STOCK"),
    GOODS_RESERVED("GOODS_RESERVED"),
    RESERVE_READY("RESERVE_READY"),
    STOCK_READY("STOCK_READY"),
    GOODS_READY_DELIVERY("GOODS_READY_DELIVERY"),
    GOODS_DELIVERY("GOODS_DELIVERY"),
    STOCK_PAY_CANCEL("STOCK_PAY_CANCEL"),
    STOCK_PAY_COMPLETE("STOCK_PAY_COMPLETE");

    private final String name;

    GoodsStatus(String message) {
        this.name = message;
    }

}
