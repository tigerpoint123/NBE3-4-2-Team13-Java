package com.app.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 외부 브로커(RabbitMQ) 적용
		registry.enableStompBrokerRelay("/topic", "/queue")
			.setRelayHost("localhost")
			.setRelayPort(5672)
			.setClientLogin("guest")
			.setClientPasscode("guest");

		// 클라이언트가 서버로 메세지를 보낼 경로
		registry.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 클라이언트가 연결할 엔드포인트 설정
		registry.addEndpoint("/ws/chat")
			.setAllowedOrigins("http://localhost:3000")
			.withSockJS();

		// notification 엔드포인트 추가
		registry.addEndpoint("/ws-notification")
				.setAllowedOrigins("*")
				.withSockJS();
	}
}
