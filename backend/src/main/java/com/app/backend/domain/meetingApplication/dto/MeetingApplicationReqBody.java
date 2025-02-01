package com.app.backend.domain.meetingApplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MeetingApplicationReqBody(
	@NotNull
	Long memberId, // TODO : memberId 입력받는게 아니라 로그인 정보에서 memberId 받는 걸로 수정
	@NotBlank
	String context
) {
}
