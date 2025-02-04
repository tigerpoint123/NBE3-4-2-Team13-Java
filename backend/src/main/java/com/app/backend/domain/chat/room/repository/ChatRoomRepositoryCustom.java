package com.app.backend.domain.chat.room.repository;

import java.util.List;
import java.util.Optional;

import com.app.backend.domain.chat.room.dto.response.ChatRoomDetailResponse;
import com.app.backend.domain.chat.room.entity.ChatRoom;

public interface ChatRoomRepositoryCustom {

	List<ChatRoom> findAllByMemberId(Long memberId);

	Optional<ChatRoomDetailResponse> findByIdWithApprovedMembers(Long id);
}
