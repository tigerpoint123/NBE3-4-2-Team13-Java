package com.app.backend.domain.chat.room.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.app.backend.domain.chat.room.dto.response.ChatRoomDetailResponse;
import com.app.backend.domain.chat.room.entity.ChatRoom;
import com.app.backend.domain.chat.room.entity.QChatRoom;
import com.app.backend.domain.group.dto.response.GroupChatResponse;
import com.app.backend.domain.group.entity.MembershipStatus;
import com.app.backend.domain.group.entity.QGroup;
import com.app.backend.domain.group.entity.QGroupMembership;
import com.app.backend.domain.member.dto.response.MemberChatResponseDto;
import com.app.backend.domain.member.entity.QMember;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	/**
	 * 멤버 Id로 참여중인 모임의 채팅방 조회
	 *
	 * @param memberId - 멤버 ID
	 * @return 채팅방 목록
	 */
	@Override
	public List<ChatRoom> findAllByMemberId(final Long memberId) {
		QChatRoom chatRoom = QChatRoom.chatRoom;
		QGroup group = QGroup.group;
		QGroupMembership groupMembership = QGroupMembership.groupMembership;

		return jpaQueryFactory
			.select(chatRoom)
			.from(chatRoom)
			.join(chatRoom.group, group)
			.join(groupMembership).on(groupMembership.group.id.eq(group.id))
			.where(groupMembership.member.id.eq(memberId))
			.distinct()
			.fetch();
	}

	/**
	 * 채팅방 정보 조회
	 * ChatRoomDetailResponse로 매핑해서 바로 반환
	 * 그룹 멤버중에서 status가 APPROVED 인 멤버만 채팅방 참여 인원으로 조회
	 *
	 * @param id - 채팅방 ID
	 * @return 채팅방 정보(그룹, 참여 멤버 정보)
	 */
	@Override
	public Optional<ChatRoomDetailResponse> findByIdWithApprovedMembers(Long id) {
		QChatRoom chatRoom = QChatRoom.chatRoom;
		QGroup group = QGroup.group;
		QGroupMembership groupMembership = QGroupMembership.groupMembership;
		QMember member = QMember.member;

		ChatRoomDetailResponse chatRoomDetailResponse = jpaQueryFactory
			.select(Projections.constructor(
				ChatRoomDetailResponse.class,
				chatRoom.id,
				Projections.constructor(
					GroupChatResponse.class,
					group.id,
					group.name,
					groupMembership.member.id.countDistinct().intValue() // APPROVED 멤버 수
				),
				Projections.list(
					Projections.constructor(
						MemberChatResponseDto.class,
						member.id,
						member.nickname,
						groupMembership.groupRole
					)
				)
			))
			.from(chatRoom)
			.join(chatRoom.group, group)
			.leftJoin(groupMembership).on(
				groupMembership.group.eq(group)
					.and(groupMembership.status.eq(MembershipStatus.APPROVED)) // 여기서 필터링
			)
			.leftJoin(groupMembership.member, member)
			.where(chatRoom.id.eq(id))
			.groupBy(chatRoom.id, group.id, group.name, member.id, member.nickname, groupMembership.groupRole)
			.fetchOne();

		return Optional.ofNullable(chatRoomDetailResponse);
	}
}
