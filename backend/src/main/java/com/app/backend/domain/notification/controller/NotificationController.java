package com.app.backend.domain.notification.controller;

import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.service.MemberService;
import com.app.backend.domain.notification.SseEmitters;
import com.app.backend.domain.notification.dto.NotificationMessage;
import com.app.backend.domain.notification.service.NotificationService;
import com.app.backend.global.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
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
        log.info("SSE subscription request received for user: {}", userId);
        
        SseEmitter emitter = new SseEmitter(30000L);
        
        try {
            sseEmitters.remove(userId);
            
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected!"));
            
            sseEmitters.add(userId, emitter);
            
            emitter.onCompletion(() -> {
                log.info("SSE connection completed for user: {}", userId);
                sseEmitters.remove(userId);
                emitter.complete();
            });
            
            emitter.onTimeout(() -> {
                log.info("SSE connection timed out for user: {}", userId);
                sseEmitters.remove(userId);
                emitter.complete();
            });
            
            emitter.onError((e) -> {
                log.error("SSE connection error for user: {}", userId, e);
                sseEmitters.remove(userId);
                emitter.complete();
            });
            
            log.info("SSE 연결 성공: userId = {}", userId);
        } catch (IOException e) {
            log.error("SSE 연결 실패: {}", e.getMessage(), e);
            sseEmitters.remove(userId);
            emitter.complete();
            throw new RuntimeException("SSE 연결 실패", e);
        }
        return emitter;
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
