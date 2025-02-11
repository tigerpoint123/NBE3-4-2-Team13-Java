package com.app.backend.domain.chat.message;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class RedisPubSubTest {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	RedisMessageListenerContainer redisMessageListenerContainer;

	@Autowired
	RedisTemplate<String,Object> redisTemplate;

	@Test // 방에 추가된 사람마다 System.out.println()이 잘 됐는지만 확인
	@DisplayName("채팅방 구독 테스트")
	public void pubSubTest() {
		ChannelTopic topic1 = new ChannelTopic("topic1");
		MessageListener subscriber1 = new MessageListener() {

			@Override
			public void onMessage(Message message, byte[] pattern) {
				try {
					String publishedMessage = objectMapper.readValue(message.getBody(), String.class);
					System.out.println("I'm subscriber1 and the message i received is : '" + publishedMessage + "'");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
		MessageListener subscriber2 = (message, pattern) -> {
			try {
				String publishedMessage = objectMapper.readValue(message.getBody(), String.class);
				System.out.println("I'm subscriber2 and the message i received is : '" + publishedMessage + "'");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		};

		//topic1에 2명의 subscriber 등록
		redisMessageListenerContainer.addMessageListener(subscriber1, topic1);
		redisMessageListenerContainer.addMessageListener(subscriber2, topic1);
		redisTemplate.convertAndSend(topic1.getTopic(), " publish Message ");
		ChannelTopic topic2 = new ChannelTopic("topic2");
		redisTemplate.convertAndSend(topic2.getTopic(), " There is no subscriber");
	}
}
