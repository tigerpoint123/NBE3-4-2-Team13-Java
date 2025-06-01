package com.app.backend.domain.notification.controller;

import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.service.MemberService;
import com.app.backend.domain.notification.SseEmitters;
import com.app.backend.domain.notification.dto.NotificationEvent;
import com.app.backend.domain.notification.dto.NotificationMessage;
import com.app.backend.domain.notification.service.NotificationService;
import com.app.backend.global.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    private final SseEmitters sseEmitters;
    private final MemberService memberService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestHeader(value = "Authorization") String token
    ) {
        Member member = memberService.getCurrentMember(token);
        String userId = String.valueOf(member.getId());

        SseEmitter emitter = new SseEmitter(30000L);

        try {
            sseEmitters.remove(userId);

            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected!")
                    .reconnectTime(30000L));

            sseEmitters.add(userId, emitter);

            new Thread(() -> {
                try {
                    while (true) {
                        Thread.sleep(15000);
                        emitter.send(SseEmitter.event()
                                .name("heartbeat")
                                .data("heartbeat")
                                .reconnectTime(30000L));
                    }
                } catch (Exception e) {
                    // Heartbeat error handling
                }
            }).start();

            emitter.onCompletion(() -> {
                sseEmitters.remove(userId);
            });

            emitter.onTimeout(() -> {
                sseEmitters.remove(userId);
            });

            emitter.onError((e) -> {
                sseEmitters.remove(userId);
            });

        } catch (Exception e) {
            sseEmitters.remove(userId);
            emitter.complete();
            throw new RuntimeException("SSE 연결 실패", e);
        }
        return emitter;
    }

    @PostMapping("/send")
    public ApiResponse<Void> sendNotification(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody NotificationEvent request
    ) {
        notificationService.sendNotification(
                request.getUserId(),
                request.getTitle(),
                request.getContent(),
                request.getType(),
                request.getId()
        );
        return ApiResponse.of(
                true,
                HttpStatus.OK,
                "알림 발행 성공"
        );
    }

    @PostMapping("/direct")
    public ApiResponse<Void> sendDirectNotification(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody NotificationEvent request
    ) {
        Member member = memberService.getCurrentMember(token);
        String userId = String.valueOf(member.getId());

        // SSE를 통한 직접 메시지 전송
        NotificationMessage message = new NotificationMessage(
                request.getId(),
                request.getUserId(),
                request.getTitle(),
                request.getContent(),
                LocalDateTime.now(),
                false
        );

        sseEmitters.sendToUser(userId, message);

        return ApiResponse.of(
                true,
                HttpStatus.OK,
                "알림 발행 성공"
        );
    }

    @GetMapping
    public ApiResponse<List<NotificationMessage>> getNotifications(
            @RequestHeader(value = "Authorization") String token
    ) {
        Member member = memberService.getCurrentMember(token);
        List<NotificationMessage> notifications =
                notificationService.getNotifications(String.valueOf(member.getId()));
        return ApiResponse.of(
                true,
                HttpStatus.OK,
                "알림 목록 조회 성공",
                notifications
        );
    }

    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> markAsRead(
            @PathVariable Long notificationId
    ) {
        notificationService.markAsRead(notificationId);
        return ApiResponse.of(
                true,
                HttpStatus.OK,
                "알림 읽음 처리 성공"
        );
    }
}
