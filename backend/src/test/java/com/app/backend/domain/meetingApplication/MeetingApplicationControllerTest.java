package com.app.backend.domain.meetingApplication;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.group.entity.Group;
import com.app.backend.domain.group.entity.RecruitStatus;
import com.app.backend.domain.group.repository.GroupRepository;
import com.app.backend.domain.meetingApplication.entity.MeetingApplication;
import com.app.backend.domain.meetingApplication.repository.MeetingApplicationRepository;
import com.app.backend.domain.meetingApplication.service.MeetingApplicationService;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.repository.MemberRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class MeetingApplicationControllerTest {

	@Autowired
	private MeetingApplicationRepository meetingApplicationRepository;

	@Autowired
	private MeetingApplicationService meetingApplicationService;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	MockMvc mvc;

	@Test
	@DisplayName("신청 폼 작성")
	void t1() throws Exception {
		// 임의로 그룹 저장
		Group group = groupRepository.save(Group.builder()
			.name("test")
			.province("test province")
			.city("test city")
			.town("test town")
			.description("test description")
			.recruitStatus(RecruitStatus.RECRUITING)
			.maxRecruitCount(10)
			.build());

		// 임의로 멤버 저장
		Member member = memberRepository.save(Member.builder()
			.username("testUser")
			.password("testPassword")
			.nickname("tester")
			.provider(Member.Provider.LOCAL)
			.role("USER")
			.disabled(false)
			.build());

		String requestJson = String.format("""
                {
                    "memberId": %d,
                    "context": "신청합니다."
                }
                """, member.getId());

		mvc.perform(MockMvcRequestBuilders.post("/api/v1/groups/" + group.getId())
				.content(requestJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value("201"))
			.andExpect(jsonPath("$.message").value(group.getId() + "번 모임에 성공적으로 가입 신청을 하셨습니다."));

		// 저장 데이터 확인
		List<MeetingApplication> applications = meetingApplicationRepository.findAll();
		assertThat(applications).hasSize(1);

		MeetingApplication application = applications.get(0);
		assertThat(application.getMember().getId()).isEqualTo(member.getId());
		assertThat(application.getGroup().getId()).isEqualTo(group.getId());
		assertThat(application.getContext()).isEqualTo("신청합니다.");
	}
}