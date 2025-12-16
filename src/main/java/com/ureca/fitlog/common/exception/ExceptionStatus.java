package com.ureca.fitlog.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * FitLog 예외 상태 정의
 *
 * 네이밍 규칙: {DOMAIN}_{LAYER}_{ERROR_TYPE}
 * 예: TODO_DOMAIN_NOT_FOUND, TODO_AUTH_UNAUTHORIZED
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@RequiredArgsConstructor
@Getter
public enum ExceptionStatus {

    // =================================================================
    // GENERIC PRESENTATION LAYER EXCEPTIONS (4xx)
    // =================================================================
    PRESENTATION_VALIDATION_INVALID_REQUEST_DATA(HttpStatus.BAD_REQUEST, "요청 데이터가 유효하지 않습니다"),
    PRESENTATION_VALIDATION_MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었습니다"),
    PRESENTATION_VALIDATION_INVALID_PATH_VARIABLE(HttpStatus.BAD_REQUEST, "경로 변수가 유효하지 않습니다"),
    PRESENTATION_HTTP_METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않는 HTTP 메서드입니다"),
    PRESENTATION_HTTP_UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 미디어 타입입니다"),

    // AUTHENTICATION & AUTHORIZATION (401, 403)
    PRESENTATION_AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    PRESENTATION_AUTH_ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),

    // =================================================================
    // TODO DOMAIN EXCEPTIONS
    // =================================================================

    // Authentication & Authorization
    TODO_AUTH_LOGIN_INFO_NOT_FOUND(HttpStatus.UNAUTHORIZED, "로그인 정보가 없습니다"),
    TODO_AUTH_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "사용자 정보를 찾을 수 없습니다"),
    TODO_AUTH_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 리소스에 대한 권한이 없습니다"),

    // Not Found
    TODO_DOMAIN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 투두를 찾을 수 없습니다"),
    TODO_DOMAIN_NOT_FOUND_OR_NO_PERMISSION(HttpStatus.NOT_FOUND, "해당 투두를 찾을 수 없거나 권한이 없습니다"),

    // Validation
    TODO_VALIDATION_REST_TIME_INVALID(HttpStatus.BAD_REQUEST, "휴식시간은 0~7200초 사이여야 합니다"),
    TODO_VALIDATION_REPS_TARGET_INVALID(HttpStatus.BAD_REQUEST, "목표 횟수는 양수여야 합니다"),
    TODO_VALIDATION_SETS_NUMBER_INVALID(HttpStatus.BAD_REQUEST, "세트 번호는 양수여야 합니다"),
    TODO_VALIDATION_WEIGHT_INVALID(HttpStatus.BAD_REQUEST, "중량은 0 이상이어야 합니다"),


    // =================================================================
    // EXERCISE DOMAIN EXCEPTIONS
    // =================================================================
    EXERCISE_DOMAIN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 운동 종목을 찾을 수 없습니다"),
    EXERCISE_DOMAIN_DUPLICATE_NAME(HttpStatus.CONFLICT, "이미 존재하는 운동 이름입니다"),
    EXERCISE_DOMAIN_NO_EXERCISES_FOUND(HttpStatus.NOT_FOUND, "해당 날짜에 운동 기록이 없습니다"),
    EXERCISE_VALIDATION_INVALID_DATE(HttpStatus.BAD_REQUEST, "날짜가 유효하지 않습니다"),
    EXERCISE_VALIDATION_INVALID_PAGE(HttpStatus.BAD_REQUEST, "페이지 번호는 0 이상이어야 합니다"),
    EXERCISE_VALIDATION_INVALID_SIZE(HttpStatus.BAD_REQUEST, "페이지 크기는 1 이상이어야 합니다"),

    // =================================================================
    // USER DOMAIN EXCEPTIONS
    // =================================================================
    USER_DOMAIN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다"),
    USER_DOMAIN_DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다"),
    USER_DOMAIN_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다"),

    // =================================================================
    // AUTH DOMAIN EXCEPTIONS
    // =================================================================
    AUTH_SIGNUP_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다"),
    AUTH_SIGNUP_DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다"),
    AUTH_SIGNUP_INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 유효하지 않습니다"),
    AUTH_LOGIN_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "존재하지 않는 사용자입니다"),
    AUTH_LOGIN_INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),
    AUTH_LOGIN_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다"),
    AUTH_TOKEN_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 생성에 실패했습니다"),

    // =================================================================
    // AUTH TOKEN EXCEPTIONS
    // =================================================================
    AUTH_TOKEN_REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 존재하지 않습니다"),
    AUTH_TOKEN_REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다"),
    AUTH_TOKEN_REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "저장된 리프레시 토큰과 일치하지 않습니다"),
    AUTH_TOKEN_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다"),

    // =================================================================
    // PROFILE DOMAIN EXCEPTIONS
    // =================================================================
    PROFILE_DOMAIN_NOT_FOUND(HttpStatus.NOT_FOUND, "프로필 정보를 찾을 수 없습니다"),
    PROFILE_VALIDATION_INVALID_BIRTH_DATE(HttpStatus.BAD_REQUEST, "생년월일이 유효하지 않습니다"),
    PROFILE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 수정에 실패했습니다"),
    INVALID_PROFILE_IMAGE(HttpStatus.BAD_REQUEST, "프로필 이미지 형식이 올바르지 않습니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기가 5MB를 초과했습니다"),
    INVALID_FILE_TYPE( HttpStatus.BAD_REQUEST,"지원하지 않는 파일 형식입니다"),
    FILE_CONVERSION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 변환에 실패했습니다"),
    PROFILE_USERNAME_REQUIRED(HttpStatus.BAD_REQUEST,"이름은 필수입니다."
    ),



    // =================================================================
    // GENERIC SERVER EXCEPTIONS (5xx)
    // =================================================================
    SERVER_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다"),
    SERVER_DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다"),
    SERVER_EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "외부 API 호출 중 오류가 발생했습니다");

    private final int statusCode;
    private final String message;

    ExceptionStatus(HttpStatus status, String message) {
        this.statusCode = status.value();
        this.message = message;
    }
}