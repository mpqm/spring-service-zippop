package com.fiiiiive.zippop.global.base;

import lombok.Getter;

@Getter
public class SuccessResponse<T> {
    private final Boolean success = true;
    private final Integer code;
    private final String message;
    private final T result;

    public SuccessResponse(SuccessCode successCode) {
        this.code = successCode.getCode();
        this.message = successCode.getMessage();
        this.result = null;
    }

    public SuccessResponse(SuccessCode successCode, T result) {
        this.code = successCode.getCode();
        this.message = successCode.getMessage();
        this.result = result;
    }
}
