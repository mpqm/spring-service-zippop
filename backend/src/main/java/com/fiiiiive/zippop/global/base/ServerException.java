package com.fiiiiive.zippop.global.base;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {
    private final ServerErrorCode errorCode;

    public ServerException(ServerErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
