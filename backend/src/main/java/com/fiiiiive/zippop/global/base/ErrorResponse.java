package com.fiiiiive.zippop.global.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final Boolean success = false;
    private final Integer status;
    private final String errorCode;
    private final Integer code;
    private final String message;
    private final Object details;
    private final String path;
    private final Instant timestamp = Instant.now();

    public ErrorResponse(ServiceErrorCode errorCode, Object details, String path) {
        this.status = 400;
        this.errorCode = errorCode.name();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.details = details;
        this.path = path;
    }

    public ErrorResponse(ServerErrorCode errorCode, String message, Object details, String path) {
        this.status = errorCode.getStatus().value();
        this.errorCode = errorCode.name();
        this.code = errorCode.getCode();
        this.message = message == null || message.isBlank() ? errorCode.getMessage() : message;
        this.details = details;
        this.path = path;
    }
}
