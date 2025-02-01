package com.app.backend.domain.meetingApplication.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.domain.meetingApplication.dto.MeetingApplicationDto;
import com.app.backend.domain.meetingApplication.entity.MeetingApplication;
import com.app.backend.domain.meetingApplication.entity.MeetingApplicationReqBody;
import com.app.backend.domain.meetingApplication.service.MeetingApplicationService;
import com.app.backend.global.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class MeetingApplicationController {

	private final MeetingApplicationService meetingApplicationService;

	@PostMapping("/{groupId}")
	public ApiResponse<MeetingApplicationDto> createMeetingApplication(
		@PathVariable Long groupId,
		@RequestBody MeetingApplicationReqBody request
	) {
		MeetingApplication meetingApplication = meetingApplicationService.create(groupId, request);

		MeetingApplicationDto meetingApplicationDto = MeetingApplicationDto.from(meetingApplication);

		return ApiResponse.of(
			true,
			"201",
			"%d번 모임에 성공적으로 가입 신청을 하셨습니다.".formatted(meetingApplication.getGroup().getId()),
			meetingApplicationDto
		);
	}

}
