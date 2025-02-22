package com.app.backend.domain.notification.service;

import com.app.backend.domain.notification.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consume(NotificationMessage message) {
        // WebSocket을 통해 클라이언트에게 알림 전송
        messagingTemplate.convertAndSendToUser(
                message.userId(),
                "/queue/notifications",
                message
        );
    }
}