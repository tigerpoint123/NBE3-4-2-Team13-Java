package com.app.backend.domain.group.exception;

import org.springframework.http.HttpStatus;

import com.app.backend.global.error.exception.DomainErrorCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum GroupLikeErrorCode implements DomainErrorCode {

    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GRL001", "모임을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "GRL002", "멤버를 찾을 수 없습니다."),
    ALREADY_LIKED(HttpStatus.CONFLICT, "GRL003", "이미 LIKE 한 그룹입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "GRL004", "LIKE 하지 않은 그룹입니다.");

    private final HttpStatus status;
    private final String     code;
    private final String     message;

}