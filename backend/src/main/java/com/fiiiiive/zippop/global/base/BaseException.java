package com.fiiiiive.zippop.global.base;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class BaseException extends Exception {

    private String message;
    private Integer code;
    private String details;

    public BaseException(BaseMessage baseMessage) {
        this.code = baseMessage.getCode();
        this.message = baseMessage.getMessage();
    }

    public BaseException(BaseMessage baseMessage, String details) {
        this.code = baseMessage.getCode();
        this.message = baseMessage.getMessage();
        this.details = details;
    }

}


