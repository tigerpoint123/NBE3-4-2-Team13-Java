package com.app.backend.domain.notification.entity;

import com.app.backend.domain.notification.dto.NotificationEvent;
import com.app.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String title;
    private String content;
    private boolean isRead;

    @Enumerated(EnumType.STRING)
    private NotificationEvent.NotificationType type;

    private Long targetId;  // 관련 엔티티 ID (그룹 ID 등)
    private LocalDateTime createdAt;

    public void markAsRead() {
        this.isRead = true;
    }
}