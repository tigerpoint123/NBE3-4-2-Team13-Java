package com.app.backend.domain.chat.message.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.app.backend.domain.chat.message.dto.request.MessageRequest;
import com.app.backend.domain.chat.message.dto.response.MessageResponse;
import com.app.backend.domain.chat.message.service.MessageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

	private final SimpMessagingTemplate messagingTemplate;
	private final MessageService messageService;

	@MessageMapping("/chat/{chatRoomId}")
	public void sendMessage(@DestinationVariable String chatRoomId, @Payload MessageRequest messageRequest) {
		MessageResponse messageResponse = messageService.saveMessage(messageRequest);
		messagingTemplate.convertAndSend(String.format("/topic/chatroom/%s", chatRoomId), messageResponse);
	}
}
