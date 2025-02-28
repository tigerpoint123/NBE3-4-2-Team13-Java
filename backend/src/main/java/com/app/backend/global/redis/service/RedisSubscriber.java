package com.app.backend.global.redis.service;

import java.io.IOException;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.app.backend.domain.chat.message.dto.response.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {

	private final SimpMessagingTemplate messagingTemplate;
	private final ObjectMapper objectMapper;
	@Override
	public void onMessage(Message message, byte[] pattern) {
		// 채널에서 chatRoomId 추출
		String channel = new String(message.getChannel());
		String chatRoomId = channel.split(":")[1];
		// log.info("Message received from Redis: {}", new String(message.getBody()));

		// byte[] 메시지를 MessageResponse 객체로 변환
		MessageResponse messageResponse = null;
		try {
			messageResponse = objectMapper.readValue(message.getBody(), MessageResponse.class);
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		// WebSocket으로 메시지 브로드캐스트
		if (messageResponse != null) {
			messagingTemplate.convertAndSend(String.format("/topic/chatroom/%s", chatRoomId), messageResponse);
		} else {
			log.error("메세지 전송 실패");
		}
	}
}
