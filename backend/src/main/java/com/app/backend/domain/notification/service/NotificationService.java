package com.app.backend.domain.notification.service;

import com.app.backend.domain.notification.SseEmitters;
import com.app.backend.domain.notification.dto.NotificationEvent;
import com.app.backend.domain.notification.dto.NotificationMessage;
import com.app.backend.domain.notification.entity.Notification;
import com.app.backend.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SseEmitters sseEmitters;

    public void sendNotification(String userId, String title, String content, NotificationEvent.NotificationType type) {
        // 알림 저장
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        // SSE를 통해 실시간 알림 전송
        NotificationMessage message = new NotificationMessage(
                userId, title, content, LocalDateTime.now()
        );
        sseEmitters.sendToUser(userId, message);
    }

    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    public List<Notification> getNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
        notification.markAsRead();
        notificationRepository.save(notification);
    }
}