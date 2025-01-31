package com.app.backend.domain.meetingApplication.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.domain.category.dto.CategoryReqBody;
import com.app.backend.domain.meetingApplication.dto.MeetingApplicationDto;
import com.app.backend.global.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class MeetingApplicationController {

	@PostMapping("/{id}")
	public ApiResponse<MeetingApplicationDto> createMeetingApplication(
		@PathVariable Long id,
		@RequestBody CategoryReqBody modifyRequest
	) {

	}

}
