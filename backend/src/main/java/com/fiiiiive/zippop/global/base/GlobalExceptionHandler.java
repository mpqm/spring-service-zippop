package com.fiiiiive.zippop.global.base;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException exception, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                exception.getErrorCode(),
                exception.getDetails(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<String> details = exception.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String field = error instanceof FieldError fieldError ? fieldError.getField() : "request";
                    return field + ": " + error.getDefaultMessage();
                })
                .toList();
        ServerErrorCode errorCode = ServerErrorCode.VALIDATION_ERROR;
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(
                errorCode, null, details, request.getRequestURI()
        ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<String> details = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();
        ServerErrorCode errorCode = ServerErrorCode.VALIDATION_ERROR;
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(
                errorCode, null, details, request.getRequestURI()
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        ServerErrorCode errorCode = ServerErrorCode.VALIDATION_ERROR;
        List<String> details = List.of(exception.getName() + ": 올바른 형식의 값을 입력해주세요.");
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(
                errorCode, null, details, request.getRequestURI()
        ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        ServerErrorCode errorCode = ServerErrorCode.MALFORMED_JSON;
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(
                errorCode, null, null, request.getRequestURI()
        ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException exception,
            HttpServletRequest request
    ) {
        if (exception instanceof BadCredentialsException) {
            ServerErrorCode errorCode = ServerErrorCode.BAD_CREDENTIALS;
            if (Objects.equals(exception.getMessage(), ServiceErrorCode.AUTH_LOGIN_FAIL_ID_NULL.getMessage())) {
                return ResponseEntity.badRequest().body(new ErrorResponse(
                        ServiceErrorCode.AUTH_LOGIN_FAIL_ID_NULL,
                        null,
                        request.getRequestURI()
                ));
            }
            if (Objects.equals(exception.getMessage(), ServiceErrorCode.AUTH_LOGIN_FAIL_PASSWORD_NULL.getMessage())) {
                return ResponseEntity.badRequest().body(new ErrorResponse(
                        ServiceErrorCode.AUTH_LOGIN_FAIL_PASSWORD_NULL,
                        null,
                        request.getRequestURI()
                ));
            }
            return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(
                    errorCode, null, null, request.getRequestURI()
            ));
        }
        if (exception instanceof DisabledException) {
            ServerErrorCode errorCode = ServerErrorCode.ACCOUNT_DISABLED;
            return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(
                    errorCode, null, null, request.getRequestURI()
            ));
        }
        ServerErrorCode errorCode = ServerErrorCode.AUTHENTICATION_REQUIRED;
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(
                errorCode, null, null, request.getRequestURI()
        ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        ServerErrorCode errorCode = ServerErrorCode.ACCESS_DENIED;
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(
                errorCode, null, null, request.getRequestURI()
        ));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwt(JwtException exception, HttpServletRequest request) {
        ServerErrorCode errorCode;
        if (exception instanceof ExpiredJwtException) {
            errorCode = ServerErrorCode.TOKEN_EXPIRED;
        } else if (exception instanceof UnsupportedJwtException) {
            errorCode = ServerErrorCode.UNSUPPORTED_TOKEN;
        } else {
            errorCode = ServerErrorCode.INVALID_TOKEN;
        }
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(
                errorCode, null, null, request.getRequestURI()
        ));
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ErrorResponse> handleServerException(ServerException exception, HttpServletRequest request) {
        ServerErrorCode errorCode = exception.getErrorCode();
        log.error("System exception on {}: {}", request.getRequestURI(), errorCode, exception.getCause());
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(
                errorCode, null, null, request.getRequestURI()
        ));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccess(DataAccessException exception, HttpServletRequest request) {
        ServerErrorCode errorCode = exception instanceof RedisConnectionFailureException
                ? ServerErrorCode.REDIS_CONNECTION_ERROR
                : ServerErrorCode.DATABASE_ERROR;
        log.error("Data access exception on {}: {}", request.getRequestURI(), errorCode, exception);
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(
                errorCode, null, null, request.getRequestURI()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        log.error("Unhandled exception on {}", request.getRequestURI(), exception);
        ServerErrorCode errorCode = ServerErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(
                errorCode, null, null, request.getRequestURI()
        ));
    }
}
