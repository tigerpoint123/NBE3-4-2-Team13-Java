package com.app.backend.domain.chat.room.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.backend.domain.chat.room.dto.response.ChatRoomListResponse;
import com.app.backend.domain.chat.room.entity.ChatRoom;
import com.app.backend.domain.chat.room.repository.ChatRoomRepository;
import com.app.backend.domain.group.entity.Group;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

	@Mock
	private ChatRoomRepository chatRoomRepository;

	@InjectMocks
	private ChatRoomService chatRoomService;

	@Test
	@DisplayName("[성공] 채팅방 목록 조회")
	void getChatRoomsByMemberId() {
		//given
		Long MemberId = 1L;

		ChatRoom chatRoom1 = ChatRoom.builder()
			.id(1L)
			.group(Group.builder().id(1L).build())
			.build();

		ChatRoom chatRoom2 = ChatRoom.builder()
			.id(2L)
			.group(Group.builder().id(3L).build())
			.build();

		List<ChatRoom> chatRooms = List.of(chatRoom1, chatRoom2);
		when(chatRoomRepository.findAllByMemberId(any(Long.class))).thenReturn(chatRooms);

		//when
		List<ChatRoomListResponse> result = chatRoomService.getChatRoomsByMemberId(MemberId);

		//then
		assertThat(result).isNotNull();
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0).chatRoomId()).isEqualTo(chatRoom1.getId());
		assertThat(result.get(0).groupId()).isEqualTo(chatRoom1.getGroup().getId());
		assertThat(result.get(1).chatRoomId()).isEqualTo(chatRoom2.getId());
		assertThat(result.get(1).groupId()).isEqualTo(chatRoom2.getGroup().getId());


	}
}