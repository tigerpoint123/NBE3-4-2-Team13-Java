package com.app.backend.domain.chat.room.repository;

import java.util.List;

import com.app.backend.domain.chat.room.entity.ChatRoom;

public interface ChatRoomRepositoryCustom {

	List<ChatRoom> findAllByMemberId(Long memberId);
}
