package com.app.backend.domain.category.exception;

import org.springframework.http.HttpStatus;

import com.app.backend.global.error.exception.DomainErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements DomainErrorCode {

	CATEGORY_NAME_DUPLICATE(HttpStatus.BAD_REQUEST, "C001", "이미 존재하는 카테고리입니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
