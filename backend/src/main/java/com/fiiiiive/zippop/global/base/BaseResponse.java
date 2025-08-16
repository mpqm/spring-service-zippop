package com.fiiiiive.zippop.global.base;
import lombok.*;

@Getter
@Setter
public class BaseResponse<T> {

    private Boolean success;
    private Integer code;
    private String message;
    private T result;

    public BaseResponse(BaseMessage baseMessage) {
        this.success = baseMessage.getSuccess();
        this.code = baseMessage.getCode();
        this.message = baseMessage.getMessage();
        this.result = null;
    }

    public BaseResponse(BaseMessage baseMessage, T result) {
        this.success = baseMessage.getSuccess();
        this.code = baseMessage.getCode();
        this.message = baseMessage.getMessage();
        this.result = result;
    }

}
