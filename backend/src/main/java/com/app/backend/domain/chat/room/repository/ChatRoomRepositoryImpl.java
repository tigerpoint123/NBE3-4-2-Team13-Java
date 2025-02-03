package com.app.backend.domain.chat.room.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.app.backend.domain.chat.room.entity.ChatRoom;
import com.app.backend.domain.chat.room.entity.QChatRoom;
import com.app.backend.domain.group.entity.QGroup;
import com.app.backend.domain.group.entity.QGroupMembership;
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
}
