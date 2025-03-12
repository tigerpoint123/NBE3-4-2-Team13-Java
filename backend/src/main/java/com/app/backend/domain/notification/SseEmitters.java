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
        SseEmitter oldEmitter = emitters.get(userId);
        if (oldEmitter != null) {
            try {
                oldEmitter.complete();
            } catch (Exception e) {
                log.error("Error completing old emitter for user {}: {}", userId, e.getMessage());
            } finally {
                emitters.remove(userId);
                log.debug("Removed existing SSE emitter for user: {}", userId);
            }
        }
        
        emitters.put(userId, emitter);
        log.debug("New SSE emitter added for user: {}. Total active emitters: {}", 
                  userId, emitters.size());
    }

    public void remove(String userId) {
        SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.error("Error completing emitter for user {}: {}", userId, e.getMessage());
            }
        }
        log.debug("Removed SSE emitter for user: {}. Total active emitters: {}", 
                  userId, emitters.size());
    }

    public void sendToUser(String userId, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                log.info("Sending notification to user {}: {}", userId, data);
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(data));
                log.info("Successfully sent notification to user: {}", userId);
            } catch (IOException e) {
                log.error("SSE 전송 실패 for user {}: {}", userId, e.getMessage());
                remove(userId);
            }
        } else {
            log.info("No SSE emitter found for user: {}", userId);
        }
    }
}