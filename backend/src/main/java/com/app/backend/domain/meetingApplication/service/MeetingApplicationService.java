package com.app.backend.domain.meetingApplication.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.group.entity.Group;
import com.app.backend.domain.group.entity.GroupMembership;
import com.app.backend.domain.group.entity.GroupRole;
import com.app.backend.domain.group.entity.MembershipStatus;
import com.app.backend.domain.group.repository.GroupMembershipRepository;
import com.app.backend.domain.group.repository.GroupRepository;
import com.app.backend.domain.meetingApplication.dto.MeetingApplicationDto;
import com.app.backend.domain.meetingApplication.dto.MeetingApplicationListDto;
import com.app.backend.domain.meetingApplication.dto.MeetingApplicationReqBody;
import com.app.backend.domain.meetingApplication.entity.MeetingApplication;
import com.app.backend.domain.meetingApplication.exception.MeetingApplicationErrorCode;
import com.app.backend.domain.meetingApplication.exception.MeetingApplicationException;
import com.app.backend.domain.meetingApplication.repository.MeetingApplicationRepository;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingApplicationService {
	private final MeetingApplicationRepository meetingApplicationRepository;
	private final GroupRepository groupRepository;
	private final MemberRepository memberRepository;
	private final GroupMembershipRepository groupMembershipRepository;

	@Transactional
	public MeetingApplication create(Long groupId, MeetingApplicationReqBody request, Long memberId) {

		Group group = groupRepository.findById(groupId)
			.orElseThrow(() -> new MeetingApplicationException(MeetingApplicationErrorCode.GROUP_NOT_FOUND));
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MeetingApplicationException(MeetingApplicationErrorCode.MEMBER_NOT_FOUND));

		int approvedMemberCount = groupMembershipRepository.countByGroupIdAndStatusAndDisabled(groupId, MembershipStatus.APPROVED, false);

		if (approvedMemberCount >= group.getMaxRecruitCount()) {
			throw new MeetingApplicationException(MeetingApplicationErrorCode.GROUP_MEMBER_LIMIT_EXCEEDED);
		}

		MeetingApplication meetingApplication = MeetingApplication.builder()
			.context(request.context())
			.group(group)
			.member(member)
			.build();

		return meetingApplicationRepository.save(meetingApplication);
	}

	// 리스트 조회
	public MeetingApplicationListDto getMeetingApplications(Long groupId, Long memberId) {

		GroupMembership membership = groupMembershipRepository.findByGroupIdAndMemberId(groupId, memberId)
			.orElseThrow(() -> new MeetingApplicationException(MeetingApplicationErrorCode.MEMBER_NOT_FOUND_IN_GROUP));

		// 권한 체크
		if (!membership.getGroupRole().equals(GroupRole.LEADER)) {
			throw new MeetingApplicationException(MeetingApplicationErrorCode.UNAUTHORIZED_ACCESS);
		}

		List<MeetingApplicationDto> applications = meetingApplicationRepository.findByGroupIdAndDisabled(groupId, false)
			.stream()
			.map(MeetingApplicationDto::from)
			.collect(Collectors.toList());

		return new MeetingApplicationListDto(applications);
	}


	// 상세 조회
	public MeetingApplicationDto getMeetingApplication(Long groupId, Long meetingApplicationId, Long memberId) {

		GroupMembership membership = groupMembershipRepository.findByGroupIdAndMemberId(groupId, memberId)
			.orElseThrow(() -> new MeetingApplicationException(MeetingApplicationErrorCode.MEMBER_NOT_FOUND_IN_GROUP));

		// 권한 체크
		if (!membership.getGroupRole().equals(GroupRole.LEADER)) {
			throw new MeetingApplicationException(MeetingApplicationErrorCode.UNAUTHORIZED_ACCESS);
		}

		MeetingApplication meetingApplication = meetingApplicationRepository.findByGroupIdAndIdAndDisabled(groupId, meetingApplicationId, false)
			.orElseThrow(() -> new MeetingApplicationException(MeetingApplicationErrorCode.MEETING_APPLICATION_NOT_FOUND));

		return MeetingApplicationDto.from(meetingApplication);
	}
}
