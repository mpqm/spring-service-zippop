package com.fiiiiive.zippop.global.base;

import com.siot.IamportRestClient.exception.IamportResponseException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<String>> handleBaseException(BaseException e){
        return ResponseEntity.badRequest().body(new BaseResponse<>(Objects.requireNonNull(BaseMessage.findByCode(e.getCode()))));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<List<String>>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.toList());

        BaseResponse<List<String>> baseResponse = new BaseResponse<>(
                BaseMessage.VALIDATION_ERROR,
                errors
        );

        return ResponseEntity.badRequest().body(baseResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<List<String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = (error instanceof FieldError) ? ((FieldError) error).getField() : "unknown";
                    return String.format("%s: %s", fieldName, error.getDefaultMessage());
                })
                .collect(Collectors.toList());

        BaseResponse<List<String>> baseResponse = new BaseResponse<>(
                BaseMessage.VALIDATION_ERROR,
                errors
        );

        return ResponseEntity.badRequest().body(baseResponse);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<BaseResponse<String>> handleMailException(MailException e){
        BaseResponse<String> baseResponse = new BaseResponse<>(BaseMessage.EMAIL_SEND_FAIL, e.getMessage());
        return ResponseEntity.badRequest().body(baseResponse);
    }

    @ExceptionHandler(IamportResponseException.class)
    public ResponseEntity<BaseResponse<String>> handleIamportResponseException(IamportResponseException e){
        BaseResponse<String> baseResponse = new BaseResponse<>(BaseMessage.INTERNAL_SERVER_ERROR, e.getMessage());
        return ResponseEntity.badRequest().body(baseResponse);
    }

    @ExceptionHandler
    public ResponseEntity<BaseResponse<String>> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BaseResponse<>(BaseMessage.ACCESS_DENIED));
    }

    @ExceptionHandler
    public ResponseEntity<BaseResponse<String>> handleAuthenticationException(AuthenticationException e) {
         if (e instanceof BadCredentialsException) {
             if(Objects.equals(e.getMessage(), BaseMessage.AUTH_LOGIN_FAIL_ID_NULL.getMessage())){
                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse<>(BaseMessage.AUTH_LOGIN_FAIL_ID_NULL, e.getMessage()));
             } else if (Objects.equals(e.getMessage(), BaseMessage.AUTH_LOGIN_FAIL_PASSWORD_NULL.getMessage())) {
                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse<>(BaseMessage.AUTH_LOGIN_FAIL_PASSWORD_NULL, e.getMessage()));
             } else {
                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse<>(BaseMessage.BAD_CREDENTIAL, e.getMessage()));
             }
        } else if (e instanceof InternalAuthenticationServiceException | e instanceof InsufficientAuthenticationException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(BaseMessage.ACCESS_DENIED, e.getMessage()));
        } else if (e instanceof DisabledException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse<>(BaseMessage.INACTIVE_MEMBER, e.getMessage()));
        }
        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(BaseMessage.INVALID_TOKEN, e.getMessage()));
        }
    }

    @ExceptionHandler
    public ResponseEntity<BaseResponse<String>> handleJwtException(JwtException e) {
        if(e instanceof ExpiredJwtException){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse<>(BaseMessage.JWT_TOKEN_EXPIRED, e.getMessage()));
        } else if(e instanceof UnsupportedJwtException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse<>(BaseMessage.JWT_TOKEN_UNSUPPORTED, e.getMessage()));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(BaseMessage.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

}