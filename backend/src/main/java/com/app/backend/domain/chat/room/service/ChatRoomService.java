package com.app.backend.domain.chat.room.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.chat.room.dto.response.ChatRoomListResponse;
import com.app.backend.domain.chat.room.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;

	public List<ChatRoomListResponse> getChatRoomsByMemberId(Long memberId) {
		return chatRoomRepository.findAllByMemberId(memberId).stream().map(ChatRoomListResponse::from).toList();
	}
}
