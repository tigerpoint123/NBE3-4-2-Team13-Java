package com.app.backend.domain.chat.room.dto.response;

import com.app.backend.domain.chat.room.entity.ChatRoom;

import lombok.Builder;

@Builder
public record ChatRoomListResponse(Long chatRoomId, Long groupId, String groupName) {

	public static ChatRoomListResponse from(ChatRoom chatRoom) {
		return ChatRoomListResponse.builder()
			.chatRoomId(chatRoom.getId())
			.groupId(chatRoom.getGroup().getId())
			.groupName(chatRoom.getGroup().getName())
			.build();
	}
}
