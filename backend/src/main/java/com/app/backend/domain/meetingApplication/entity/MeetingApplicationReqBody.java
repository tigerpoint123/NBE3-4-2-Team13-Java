package com.app.backend.domain.meetingApplication.entity;

import jakarta.validation.constraints.NotBlank;

public record MeetingApplicationReqBody(
	@NotBlank
	Long groupId,
	@NotBlank
	String context
) {
}
