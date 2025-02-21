package com.app.backend.domain.notification.service;

import com.app.backend.domain.notification.NotificationEvent;
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
    private final NotificationProducer notificationProducer;

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

        // 실시간 알림 전송
        NotificationMessage message = new NotificationMessage(
                userId, title, content, LocalDateTime.now()
        );
        notificationProducer.sendNotification(message);
    }

    public void save(Notification notification) {

    }

    public List<Notification> getNotifications(String userId) {

        return null;
    }

    public void markAsRead(Long notificationId) {

    }
}