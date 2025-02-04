package com.app.backend.domain.chat.room.dto.response;

import java.util.List;

import com.app.backend.domain.group.dto.response.GroupChatResponse;
import com.app.backend.domain.member.dto.response.MemberChatResponseDto;

public record ChatRoomDetailResponse(Long chatRoomId, GroupChatResponse group, List<MemberChatResponseDto> members) {

}
