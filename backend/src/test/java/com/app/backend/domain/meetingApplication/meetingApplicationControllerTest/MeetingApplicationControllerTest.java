package com.app.backend.domain.meetingApplication.meetingApplicationControllerTest;

import static org.mockito.BDDMockito.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.app.backend.domain.group.entity.Group;
import com.app.backend.domain.group.entity.GroupMembership;
import com.app.backend.domain.group.entity.GroupRole;
import com.app.backend.domain.group.entity.RecruitStatus;
import com.app.backend.domain.group.repository.GroupMembershipRepository;
import com.app.backend.domain.group.repository.GroupRepository;
import com.app.backend.domain.meetingApplication.dto.MeetingApplicationReqBody;
import com.app.backend.domain.meetingApplication.entity.MeetingApplication;
import com.app.backend.domain.meetingApplication.exception.MeetingApplicationErrorCode;
import com.app.backend.domain.meetingApplication.exception.MeetingApplicationException;
import com.app.backend.domain.meetingApplication.service.MeetingApplicationService;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.entity.MemberDetails;
import com.app.backend.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MeetingApplicationControllerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private GroupMembershipRepository groupMembershipRepository;

	@MockitoBean
	private MeetingApplicationService meetingApplicationService;

	@Autowired
	MockMvc mvc;

	private Group group;
	private Member member;

	@BeforeEach
	void setup() {
		group = groupRepository.save(Group.builder()
			.name("test group")
			.province("test province")
			.city("test city")
			.town("test town")
			.description("test description")
			.recruitStatus(RecruitStatus.RECRUITING)
			.maxRecruitCount(1)
			.build());

		member = Member.builder()
			.username("testUser")
			.nickname("testNickname")
			.role("USER")
			.disabled(false)
			.build();

		groupRepository.save(group);
		memberRepository.save(member);
	}

	@Test
	@DisplayName("신청 폼 작성")
	void t1() throws Exception {
		MemberDetails mockUser = new MemberDetails(member);

		MeetingApplicationReqBody request = new MeetingApplicationReqBody("신청합니다.");

		MeetingApplication mockMeetingApplication = MeetingApplication.builder()
			.id(1L)
			.context("신청합니다.")
			.group(group)
			.member(member)
			.build();

		given(meetingApplicationService.create(group.getId(), request, member.getId()))
			.willReturn(mockMeetingApplication);

		// When
		ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/v1/groups/{groupId}", group.getId())
			.with(user(mockUser))  // MockUser를 사용하여 로그인된 상태 시뮬레이션
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)) // 요청 본문
		);

		// Then
		resultActions.andExpect(status().isCreated());
		resultActions.andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()));
		resultActions.andExpect(jsonPath("$.message")
			.value(group.getId() + "번 모임에 성공적으로 가입 신청을 하셨습니다."));
		resultActions.andExpect(jsonPath("$.data.context").value("신청합니다."));
		resultActions.andDo(print());

		verify(meetingApplicationService, times(1)).create(group.getId(), request, member.getId());

	}



}
