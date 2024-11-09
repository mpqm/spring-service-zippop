package com.fiiiiive.zippop.global.common.exception;

import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.siot.IamportRestClient.exception.IamportResponseException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<String>> handleBaseException(BaseException e){
        return ResponseEntity.badRequest().body(new BaseResponse(BaseResponseMessage.findByCode(e.getCode())));
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<BaseResponse> handleMailException(MailException e){
        BaseResponse<String> baseResponse = new BaseResponse<>(BaseResponseMessage.MEMBER_EMAIL_SEND_FAIL, e.getMessage());
        return ResponseEntity.badRequest().body(baseResponse);
    }

    @ExceptionHandler(IamportResponseException.class)
    public ResponseEntity<BaseResponse> handleIamportResponseException(IamportResponseException e){
        BaseResponse<String> baseResponse = new BaseResponse<>(BaseResponseMessage.INTERNAL_SERVER_ERROR, e.getMessage());
        return ResponseEntity.badRequest().body(baseResponse);
    }

    @ExceptionHandler
    public ResponseEntity<BaseResponse<String>> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BaseResponse(BaseResponseMessage.ACCESS_DENIED));
    }

    @ExceptionHandler
    public ResponseEntity<BaseResponse<String>> handleAuthenticationException(AuthenticationException e) {
        if (e instanceof BadCredentialsException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(BaseResponseMessage.BAD_CREDENTIAL, e.getMessage()));
        } else if (e instanceof InternalAuthenticationServiceException | e instanceof InsufficientAuthenticationException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(BaseResponseMessage.CHAT_USER_NOT_FOUND, e.getMessage()));
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(BaseResponseMessage.INVALID_TOKEN, e.getMessage()));
        }
    }

    @ExceptionHandler
    public ResponseEntity<BaseResponse<String>> handleJwtException(JwtException e) {
        if(e instanceof ExpiredJwtException){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(BaseResponseMessage.JWT_TOKEN_EXPIRED, e.getMessage()));
        } else if(e instanceof UnsupportedJwtException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(BaseResponseMessage.JWT_TOKEN_UNSUPPORTED, e.getMessage()));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(BaseResponseMessage.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }
}