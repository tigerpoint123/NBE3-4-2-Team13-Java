package com.app.backend.domain.chat.room.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.chat.room.entity.ChatRoom;
import com.app.backend.domain.chat.room.repository.ChatRoomRepository;
import com.app.backend.domain.chat.room.service.ChatRoomService;
import com.app.backend.domain.group.entity.Group;
import com.app.backend.domain.group.entity.GroupMembership;
import com.app.backend.domain.group.entity.GroupRole;
import com.app.backend.domain.group.entity.RecruitStatus;
import com.app.backend.domain.group.repository.GroupMembershipRepository;
import com.app.backend.domain.group.repository.GroupRepository;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.entity.MemberDetails;
import com.app.backend.domain.member.repository.MemberRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ChatRoomMemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ChatRoomService chatRoomService;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupMembershipRepository groupMembershipRepository;

	@Test
	@DisplayName("[성공] 채팅방 목록 조회")
	void getChatRoomsByMemberId() throws Exception {
		// given

		// 멤버 생성
		Member member = createMember("testUser", "testNickname");

		// 멤버 저장
		Member savedMember = memberRepository.save(member);

		// 그룹 생성
		Group group1 = createGroup("대구fc 팬 모임1", "대구 광역시", "북구", "고성로 191", "대팍 직관 같이가실분");
		Group group2 = createGroup("대구fc 팬 모임2", "대구 광역시", "북구", "고성로 191", "대팍 직관 같이 가실분2");

		// 그룹 저장
		groupRepository.save(group1);
		groupRepository.save(group2);

		// 그룹과 멤버십 관계 설정
		GroupMembership groupMembership1 = createGroupMembership(savedMember, group1, GroupRole.LEADER);
		GroupMembership groupMembership2 = createGroupMembership(savedMember, group2, GroupRole.LEADER);

		// 멤버십 저장
		groupMembershipRepository.save(groupMembership1);
		groupMembershipRepository.save(groupMembership2);

		// 채팅방 생성 및 그룹에 연결
		ChatRoom chatRoom1 = createChatRoom(group1);
		ChatRoom chatRoom2 = createChatRoom(group2);
		chatRoomRepository.save(chatRoom1);
		chatRoomRepository.save(chatRoom2);

		// 그룹에 채팅방 연결
		group1.setChatRoom(chatRoom1);
		group2.setChatRoom(chatRoom2);

		// AuthenticationPrincipal의 MemberDetails 객체를 설정
		MemberDetails memberDetails = new MemberDetails(savedMember);
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
			memberDetails, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/members/chatrooms"));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.message").value("채팅방 목록 조회 성공"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data.length()").value(2)) // 두 개의 채팅방
			.andExpect(jsonPath("$.data[0].chatRoomId").value(1))
			.andExpect(jsonPath("$.data[0].groupId").value(1))
			.andExpect(jsonPath("$.data[0].groupName").value("대구fc 팬 모임1"))
			.andExpect(jsonPath("$.data[1].chatRoomId").value(2))
			.andExpect(jsonPath("$.data[1].groupId").value(2))
			.andExpect(jsonPath("$.data[1].groupName").value("대구fc 팬 모임2"));;
	}

	private Member createMember(String username, String nickname) {
		return Member.builder()
			.username(username)
			.nickname(nickname)
			.role("USER")
			.build();
	}

	private Group createGroup(String name, String province, String city, String town, String description) {
		return Group.builder()
			.name(name)
			.province(province)
			.city(city)
			.town(town)
			.description(description)
			.recruitStatus(RecruitStatus.RECRUITING)
			.maxRecruitCount(10)
			.build();
	}

	private GroupMembership createGroupMembership(Member member, Group group, GroupRole groupRole) {
		GroupMembership groupMembership = GroupMembership.builder()
			.member(member)
			.group(group)
			.groupRole(groupRole)
			.build();
		groupMembershipRepository.save(groupMembership);
		return groupMembership;
	}

	private ChatRoom createChatRoom(Group group) {
		ChatRoom chatRoom = ChatRoom.builder().group(group).build();
		chatRoomRepository.save(chatRoom);
		return chatRoom;
	}

}