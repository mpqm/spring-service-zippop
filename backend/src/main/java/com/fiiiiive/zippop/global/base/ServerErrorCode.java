package com.fiiiiive.zippop.global.base;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ServerErrorCode {
    REQUEST_FAILED(300, HttpStatus.BAD_REQUEST, "요청을 처리할 수 없습니다."),
    DATABASE_ERROR(301, HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 처리 중 오류가 발생했습니다."),
    INVALID_TOKEN(302, HttpStatus.UNAUTHORIZED, "인증 정보가 올바르지 않습니다. 다시 로그인해주세요."),
    TOKEN_EXPIRED(303, HttpStatus.UNAUTHORIZED, "로그인이 만료되었습니다. 다시 로그인해주세요."),
    UNSUPPORTED_TOKEN(304, HttpStatus.UNAUTHORIZED, "지원하지 않는 인증 형식입니다."),
    MALFORMED_TOKEN(305, HttpStatus.UNAUTHORIZED, "인증 정보가 손상되었습니다."),
    BAD_CREDENTIALS(307, HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호를 확인해주세요."),
    FILE_UPLOAD_ERROR(309, HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    AUTHENTICATION_REQUIRED(310, HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    EMAIL_SEND_ERROR(311, HttpStatus.BAD_GATEWAY, "이메일 전송에 실패했습니다."),
    MALFORMED_JSON(312, HttpStatus.BAD_REQUEST, "요청 본문 형식이 올바르지 않습니다."),
    PAYMENT_PROVIDER_ERROR(314, HttpStatus.BAD_GATEWAY, "결제 서비스 처리 중 오류가 발생했습니다."),
    VALIDATION_ERROR(315, HttpStatus.BAD_REQUEST, "입력값을 확인해주세요."),
    ACCOUNT_DISABLED(316, HttpStatus.UNAUTHORIZED, "비활성화된 계정입니다."),
    ACCESS_DENIED(403, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    REDIS_CONNECTION_ERROR(9000, HttpStatus.SERVICE_UNAVAILABLE, "Redis 연결 중 오류가 발생했습니다."),
    REDIS_SAVE_ERROR(9001, HttpStatus.INTERNAL_SERVER_ERROR, "Redis 데이터 저장 중 오류가 발생했습니다."),
    REDIS_READ_ERROR(9002, HttpStatus.INTERNAL_SERVER_ERROR, "Redis 데이터 조회 중 오류가 발생했습니다."),
    REDIS_DELETE_ERROR(9003, HttpStatus.INTERNAL_SERVER_ERROR, "Redis 데이터 삭제 중 오류가 발생했습니다."),
    REDIS_QUEUE_CREATE_ERROR(9004, HttpStatus.INTERNAL_SERVER_ERROR, "Redis 큐 생성 중 오류가 발생했습니다."),
    REDIS_QUEUE_ENROLL_ERROR(9005, HttpStatus.INTERNAL_SERVER_ERROR, "Redis 큐 등록 중 오류가 발생했습니다."),
    REDIS_QUEUE_SIZE_ERROR(9006, HttpStatus.INTERNAL_SERVER_ERROR, "Redis 큐 크기 조회 중 오류가 발생했습니다."),
    REDIS_QUEUE_UPDATE_ERROR(9007, HttpStatus.INTERNAL_SERVER_ERROR, "Redis 큐 갱신 중 오류가 발생했습니다."),
    REDIS_QUEUE_ORDER_ERROR(9008, HttpStatus.INTERNAL_SERVER_ERROR, "Redis 큐 순위 조회 중 오류가 발생했습니다."),
    REDIS_QUEUE_REMOVE_ERROR(9009, HttpStatus.INTERNAL_SERVER_ERROR, "Redis 큐 삭제 중 오류가 발생했습니다."),
    REDIS_QUEUE_FIRST_USER_ERROR(9010, HttpStatus.INTERNAL_SERVER_ERROR, "Redis 대기열 처리 중 오류가 발생했습니다.");

    private final Integer code;
    private final HttpStatus status;
    private final String message;

    ServerErrorCode(Integer code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
