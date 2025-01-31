package com.app.backend.domain.meetingApplication.exception;

import org.springframework.http.HttpStatus;

import com.app.backend.global.error.exception.DomainErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingApplicationErrorCode implements DomainErrorCode {

	GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "MA001", "해당 id의 그룹은 존재하지 않습니다."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MA002", "해당 id의 멤버는 존재하지 않습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
