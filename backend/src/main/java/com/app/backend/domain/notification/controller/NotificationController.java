package com.app.backend.domain.notification.controller;

import com.app.backend.domain.notification.entity.Notification;
import com.app.backend.domain.notification.service.NotificationService;
import com.app.backend.global.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<Notification>> getNotifications(@RequestParam String userId) {
        List<Notification> notifications = notificationService.getNotifications(userId);
        return ApiResponse.of(
                true,
                HttpStatus.OK,
                "알림 목록 조회 성공",
                notifications
        );
    }

    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ApiResponse.of(
                true,
                HttpStatus.OK,
                "알림 읽음 처리 성공"
        );
    }
}
