package com.app.backend.domain.meetingApplication.service;

import org.springframework.stereotype.Service;

import com.app.backend.domain.group.entity.Group;
import com.app.backend.domain.group.repository.GroupRepository;
import com.app.backend.domain.meetingApplication.entity.MeetingApplication;
import com.app.backend.domain.meetingApplication.entity.MeetingApplicationReqBody;
import com.app.backend.domain.meetingApplication.exception.MeetingApplicationErrorCode;
import com.app.backend.domain.meetingApplication.exception.MeetingApplicationException;
import com.app.backend.domain.meetingApplication.repository.MeetingApplicationRepository;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingApplicationService {
	private final MeetingApplicationRepository meetingApplicationRepository;
	private final GroupRepository groupRepository;
	private final MemberRepository memberRepository;

	public MeetingApplication create(Long groupId, MeetingApplicationReqBody request) {

		Group group = groupRepository.findById(groupId)
			.orElseThrow(() -> new MeetingApplicationException(MeetingApplicationErrorCode.GROUP_NOT_FOUND));
		Member member = memberRepository.findById(request.memberId())
			.orElseThrow(() -> new MeetingApplicationException(MeetingApplicationErrorCode.MEMBER_NOT_FOUND));

		MeetingApplication meetingApplication = MeetingApplication.builder()
			.context(request.context())
			.group(group)
			.member(member)
			.build();

		return meetingApplicationRepository.save(meetingApplication);
	}
}
