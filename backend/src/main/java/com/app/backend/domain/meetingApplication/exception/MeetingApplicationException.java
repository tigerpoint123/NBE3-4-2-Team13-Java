package com.app.backend.domain.meetingApplication.exception;

import com.app.backend.global.error.exception.DomainErrorCode;
import com.app.backend.global.error.exception.DomainException;

import lombok.RequiredArgsConstructor;

public class MeetingApplicationException extends DomainException {

	private final MeetingApplicationErrorCode errorCode;

	public MeetingApplicationException(final MeetingApplicationErrorCode errorCode) {
		super(errorCode);  // 부모 클래스의 생성자에 MeetingApplicationErrorCode를 넘겨줌
		this.errorCode = errorCode;
	}

	public MeetingApplicationErrorCode getErrorCode() {
		return errorCode;
	}
}