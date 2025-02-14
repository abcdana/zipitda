package com.danahub.zipitda.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 공통 에러 코드 정의
 */
public enum ErrorType {

    // 400 BAD REQUEST - 클라이언트 요청 오류
    INVALID_REQUEST(40000, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    MISSING_REQUIRED_VALUE(40001, HttpStatus.BAD_REQUEST, "필수 입력값이 누락되었습니다."),
    UNSUPPORTED_REQUEST_TYPE(40002, HttpStatus.BAD_REQUEST, "지원하지 않는 요청 형식입니다."),

    // 401 UNAUTHORIZED - 인증 실패
    UNAUTHORIZED(40100, HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    INVALID_CREDENTIALS(40101, HttpStatus.UNAUTHORIZED, "잘못된 인증 정보입니다."),
    TOKEN_EXPIRED(40102, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    ACCOUNT_DISABLED(40103, HttpStatus.UNAUTHORIZED, "비활성화된 계정입니다."),

    // 403 FORBIDDEN - 접근 제한
    ACCESS_DENIED(40300, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    ADMIN_ONLY(40301, HttpStatus.FORBIDDEN, "관리자만 접근 가능합니다."),

    // 404 NOT FOUND - 리소스 찾을 수 없음
    RESOURCE_NOT_FOUND(40400, HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(40401, HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    VERIFICATION_NOT_FOUND(40402, HttpStatus.NOT_FOUND, "인증 요청이 존재하지 않습니다."),
    PAYMENT_NOT_FOUND(40403, HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),

    // 409 CONFLICT - 리소스 충돌
    DUPLICATE_EMAIL(40900, HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    DUPLICATE_USERNAME(40901, HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    ALREADY_VERIFIED(40902, HttpStatus.CONFLICT, "이미 인증이 완료된 요청입니다."),

    // 410 GONE - 리소스 만료
    VERIFICATION_EXPIRED(41000, HttpStatus.GONE, "인증 코드가 만료되었습니다."),

    // 422 UNPROCESSABLE ENTITY - 요청 검증 실패
    INVALID_EMAIL_FORMAT(42200, HttpStatus.UNPROCESSABLE_ENTITY, "유효하지 않은 이메일 형식입니다."),
    INVALID_PHONE_NUMBER(42201, HttpStatus.UNPROCESSABLE_ENTITY, "유효하지 않은 전화번호 형식입니다."),
    INVALID_VERIFICATION_CODE(42202, HttpStatus.UNPROCESSABLE_ENTITY, "인증 코드가 일치하지 않습니다."),

    // 500 INTERNAL SERVER ERROR - 서버 오류
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),
    DATABASE_ERROR(50001, HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다."),
    EXTERNAL_API_ERROR(50002, HttpStatus.INTERNAL_SERVER_ERROR, "외부 API 호출 중 오류가 발생했습니다."),

    // 503 SERVICE UNAVAILABLE - 서버 사용 불가
    SERVICE_UNAVAILABLE(50300, HttpStatus.SERVICE_UNAVAILABLE, "현재 서비스 이용이 불가능합니다.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorType(int errorCode, HttpStatus httpStatus, String message) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}