package com.app.backend.domain.meetingApplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MeetingApplicationReqBody(
	@NotBlank
	String context
) {
}
