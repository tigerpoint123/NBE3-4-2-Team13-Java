package com.app.backend.domain.notification.dto;

import java.time.LocalDateTime;

public record NotificationMessage(
        String userId,
        String title,
        String content,
        LocalDateTime timestamp
) {
}
