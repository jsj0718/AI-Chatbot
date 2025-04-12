package com.ai.chatbot.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ServiceErrorCode {
    /** 회원 */
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    /** 인증 & 인가 */
    INVALID_EMAIL(HttpStatus.NOT_FOUND, "존재하지 않는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "권한 정보가 잘못되었습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    /** 채팅 */
    INVALID_MODEL(HttpStatus.BAD_REQUEST, "지원하지 않는 모델입니다."),
    CHAT_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "채팅 생성에 실패했습니다."),
    THREAD_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 스레드를 찾을 수 없습니다."),
    THREAD_FORBIDDEN(HttpStatus.FORBIDDEN, "자신의 스레드만 삭제할 수 있습니다."),
    CHAT_HISTORY_EMPTY(HttpStatus.NO_CONTENT, "해당 스레드에는 대화 기록이 없습니다."),
    CHAT_NOT_FOUND(HttpStatus.NOT_FOUND, "대화가 존재하지 않습니다."),

    /** 피드백 */
    FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "피드백을 찾을 수 없습니다."),
    FEEDBACK_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 피드백이 존재합니다."),
    UNAUTHORIZED_FEEDBACK_ACCESS(HttpStatus.FORBIDDEN, "본인 대화에만 피드백을 남길 수 있습니다."),
    FEEDBACK_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "피드백 상태는 관리자만 변경할 수 있습니다."),

    /** 관리자 */
    CSV_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CSV 보고서 생성 중 오류가 발생했습니다."),

    ;

    private final HttpStatus status;
    private final String message;

    ServiceErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}