package com.app.backend.domain.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SseEmitters {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void add(String userId, SseEmitter emitter) {
        log.info("Adding new SSE emitter for user: {}", userId);
        emitters.put(userId, emitter);
        log.info("Current active emitters count: {}", emitters.size());
    }

    public void remove(String userId) {
        emitters.remove(userId);
    }

    public void sendToUser(String userId, Object data) {
        log.info("Attempting to send SSE to user: {}", userId);
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                log.info("Found emitter for user: {}, sending data: {}", userId, data);
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(data));
                log.info("Successfully sent SSE to user: {}", userId);
            } catch (IOException e) {
                log.error("SSE 전송 실패 for user {}: {}", userId, e.getMessage(), e);
                emitters.remove(userId);
            }
        } else {
            log.warn("No SSE emitter found for user: {}", userId);
        }
    }
}