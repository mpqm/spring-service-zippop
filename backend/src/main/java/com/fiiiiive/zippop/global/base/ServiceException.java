package com.fiiiiive.zippop.global.base;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final ServiceErrorCode errorCode;
    private final String details;

    public ServiceException(ServiceErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    public ServiceException(ServiceErrorCode errorCode, String details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }
}
