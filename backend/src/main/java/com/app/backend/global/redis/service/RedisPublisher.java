package com.app.backend.global.redis.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.app.backend.domain.chat.message.dto.response.MessageResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPublisher {

	private final RedisTemplate<String, Object> redisTemplate;

	// 채널에 메세지 발행
	public void publish(String chatRoomId, MessageResponse messageResponse) {
		// Redis 에 메세지 발행
		log.info("publishing message to topic: chatroom:{}", chatRoomId);
		redisTemplate.convertAndSend("chatroom:" + chatRoomId, messageResponse);
	}
}
