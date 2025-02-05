package com.app.backend.domain.chat.util;

import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.app.backend.domain.chat.message.entity.Message;
import com.app.backend.domain.chat.message.repository.MessageRepository;
import com.app.backend.domain.chat.room.entity.ChatRoom;
import com.app.backend.domain.chat.room.repository.ChatRoomRepository;
import com.app.backend.domain.group.entity.Group;
import com.app.backend.domain.group.entity.GroupMembership;
import com.app.backend.domain.group.entity.GroupRole;
import com.app.backend.domain.group.entity.RecruitStatus;
import com.app.backend.domain.group.repository.GroupMembershipRepository;
import com.app.backend.domain.group.repository.GroupRepository;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.entity.MemberDetails;
import com.app.backend.domain.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class TestDataUtil {

	private final MemberRepository memberRepository;
	private final GroupRepository groupRepository;
	private final GroupMembershipRepository groupMembershipRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final MessageRepository messageRepository;

	public Member createAndSaveMember(String username, String nickname) {
		Member member = Member.builder()
			.username(username)
			.nickname(nickname)
			.role("USER")
			.build();
		return memberRepository.save(member);
	}

	public Group createAndSaveGroup(String name, String province, String city, String town, String description) {
		Group group = Group.builder()
			.name(name)
			.province(province)
			.city(city)
			.town(town)
			.description(description)
			.recruitStatus(RecruitStatus.RECRUITING)
			.maxRecruitCount(10)
			.build();
		return groupRepository.save(group);
	}

	public GroupMembership createAndSaveGroupMembership(Member member, Group group, GroupRole groupRole) {
		GroupMembership groupMembership = GroupMembership.builder()
			.member(member)
			.group(group)
			.groupRole(groupRole)
			.build();
		groupMembershipRepository.save(groupMembership);
		return groupMembership;
	}

	public void saveGroupMembership(GroupMembership groupMembership) {
		groupMembershipRepository.save(groupMembership);
	}

	public ChatRoom createAndSaveChatRoom(Group group) {
		ChatRoom chatRoom = ChatRoom.builder().group(group).build();
		return chatRoomRepository.save(chatRoom);
	}

	public void setAuthentication(Member member) {
		MemberDetails memberDetails = new MemberDetails(member);
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
			memberDetails, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
	}
}
