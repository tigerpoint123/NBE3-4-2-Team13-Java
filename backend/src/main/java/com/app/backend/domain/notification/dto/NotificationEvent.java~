package com.app.backend.domain.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationEvent {
    private String userId;
    private String title;
    private String content;
    private NotificationType type;

    public enum NotificationType {
        GROUP_INVITE,
        NEW_MESSAGE,
        GROUP_UPDATE
    }
}
