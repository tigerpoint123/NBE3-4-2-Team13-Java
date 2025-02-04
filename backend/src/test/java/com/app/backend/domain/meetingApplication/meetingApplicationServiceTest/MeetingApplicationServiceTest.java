package com.app.backend.domain.meetingApplication.meetingApplicationServiceTest;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.app.backend.domain.group.entity.Group;
import com.app.backend.domain.group.entity.GroupMembership;
import com.app.backend.domain.group.entity.GroupRole;
import com.app.backend.domain.group.entity.MembershipStatus;
import com.app.backend.domain.group.entity.RecruitStatus;
import com.app.backend.domain.group.repository.GroupMembershipRepository;
import com.app.backend.domain.group.repository.GroupRepository;
import com.app.backend.domain.meetingApplication.dto.MeetingApplicationReqBody;
import com.app.backend.domain.meetingApplication.entity.MeetingApplication;
import com.app.backend.domain.meetingApplication.exception.MeetingApplicationErrorCode;
import com.app.backend.domain.meetingApplication.exception.MeetingApplicationException;
import com.app.backend.domain.meetingApplication.repository.MeetingApplicationRepository;
import com.app.backend.domain.meetingApplication.service.MeetingApplicationService;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.repository.MemberRepository;

@SpringBootTest
public class MeetingApplicationServiceTest {

	@Autowired
	private MeetingApplicationService meetingApplicationService;

	@Autowired
	private MeetingApplicationRepository meetingApplicationRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupMembershipRepository groupMembershipRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("Fail : 그룹 정원 초과 시 예외 처리")
	void t1() {
		// Given
		Group group = groupRepository.save(Group.builder()
			.name("test group")
			.province("test province")
			.city("test city")
			.town("test town")
			.description("test description")
			.recruitStatus(RecruitStatus.RECRUITING)
			.maxRecruitCount(1)  // 정원을 1로 설정
			.build());

		Member member1 = memberRepository.save(Member.builder()
			.username("testUser1")
			.nickname("nickname1")
			.role("USER")
			.disabled(false)
			.build());

		Member member2 = memberRepository.save(Member.builder()
			.username("testUser2")
			.nickname("nickname2")
			.role("USER")
			.disabled(false)
			.build());

		// 1명만 그룹에 추가
		groupMembershipRepository.save(GroupMembership.builder()
			.group(group)
			.member(member1)
			.groupRole(GroupRole.LEADER)
			.build());


		MeetingApplicationReqBody request = new MeetingApplicationReqBody("Test Application");

		// When & Then
		assertThatThrownBy(() -> meetingApplicationService.create(group.getId(), request, member2.getId()))
			.isInstanceOf(MeetingApplicationException.class)
			.hasFieldOrPropertyWithValue("domainErrorCode", MeetingApplicationErrorCode.GROUP_MEMBER_LIMIT_EXCEEDED)
			.hasMessage("그룹 정원이 초과되었습니다.");

		// MeetingApplication 저장 X
		List<MeetingApplication> applications = meetingApplicationRepository.findByGroupId(group.getId());
		assertThat(applications).isNotNull();
		assertThat(applications.size()).isEqualTo(0);

		// 그룹 승인 멤버는 1명이여야 함
		int approvedMembershipCount = groupMembershipRepository.countByGroupIdAndStatusAndDisabled(group.getId(), MembershipStatus.APPROVED, false);
		assertThat(approvedMembershipCount).isEqualTo(1);
	}

}