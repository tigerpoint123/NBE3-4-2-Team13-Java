package com.app.backend.domain.meetingApplication.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MeetingApplicationReqBody(
	@NotNull
	Long memberId, // TODO : jwt 토큰에서 memberId 추출하는 방식으로 수정
	@NotBlank
	String context
) {
}
