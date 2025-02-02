package com.app.backend.domain.meetingApplication;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.group.entity.Group;
import com.app.backend.domain.group.entity.RecruitStatus;
import com.app.backend.domain.group.repository.GroupRepository;
import com.app.backend.domain.meetingApplication.dto.MeetingApplicationReqBody;
import com.app.backend.domain.meetingApplication.entity.MeetingApplication;
import com.app.backend.domain.meetingApplication.repository.MeetingApplicationRepository;
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
	private MeetingApplicationRepository meetingApplicationRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private MemberRepository memberRepository; // 추가

	@MockitoBean // 이 어노테이션을 사용해 mock 객체를 자동으로 생성
	private MeetingApplicationService meetingApplicationService;


	@Autowired
	MockMvc mvc;

	private Group group;
	private Member member;

	@BeforeEach
	void setUp() {
		group = groupRepository.save(Group.builder()
			.name("test group")
			.province("test province")
			.city("test city")
			.town("test town")
			.description("test description")
			.recruitStatus(RecruitStatus.RECRUITING)
			.maxRecruitCount(10)
			.build());

		member = memberRepository.save(Member.builder()
			.username("testUser")
			.nickname("test_nickname")
			.role("USER")
			.disabled(false)
			.build());
	}

	@Test
	@DisplayName("신청 폼 작성")
	void t1() throws Exception {
		groupRepository.flush();  // 트랜잭션이 DB에 반영되도록 강제 플러시
		memberRepository.flush(); // 멤버도 DB에 반영

		Optional<Group> foundGroup = groupRepository.findById(group.getId());
		System.out.println("찾은 그룹: " + foundGroup.orElse(null)); // 로그 출력

		MemberDetails mockUser = new MemberDetails(member);

		// 모임 신청 내용 설정 (memberId 제거됨)
		MeetingApplicationReqBody request = new MeetingApplicationReqBody("신청합니다.");

		// Mock 객체 및 메서드 설정
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
	}
}