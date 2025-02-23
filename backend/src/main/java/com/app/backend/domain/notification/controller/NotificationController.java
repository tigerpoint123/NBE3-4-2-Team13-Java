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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        try {
            // 연결 직후 더미 이벤트 전송 (연결 확인용)
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected!"));
            
            sseEmitters.add(userId, emitter);
            
            // 연결 종료 시 처리
            emitter.onCompletion(() -> sseEmitters.remove(userId));
            emitter.onTimeout(() -> sseEmitters.remove(userId));
            emitter.onError((e) -> sseEmitters.remove(userId));
            
            log.info("SSE 연결 성공: userId = {}", userId);
        } catch (IOException e) {
            log.error("SSE 연결 실패: {}", e.getMessage());
            emitter.complete();
        }
        return emitter;
    }

    @GetMapping
    public ApiResponse<List<NotificationMessage>> getNotifications(
            @RequestHeader(value = "Authorization") String token
    ) {
        Member member = memberService.getCurrentMember(token); // 현재 사용자 조회
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
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody NotificationMessage notification
    ) {
        notificationService.markAsRead(notificationId);
        return ApiResponse.of(
                true,
                HttpStatus.OK,
                "알림 읽음 처리 성공"
        );
    }
}
