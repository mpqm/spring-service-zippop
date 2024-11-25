package com.fiiiiive.zippop.global.common.responses;
import lombok.*;

@Getter
@Setter
public class BaseResponse<T> {
    private Boolean success;
    private Integer code;
    private String message;
    private T result;


    public BaseResponse(BaseResponseMessage baseResponseMessage) {
        this.success = baseResponseMessage.getSuccess();
        this.code = baseResponseMessage.getCode();
        this.message = baseResponseMessage.getMessage();
        this.result = null;
    }

    public BaseResponse( BaseResponseMessage baseResponseMessage, T result) {
        this.success = baseResponseMessage.getSuccess();
        this.code = baseResponseMessage.getCode();
        this.message = baseResponseMessage.getMessage();
        this.result = result;
    }
}
