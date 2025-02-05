package com.app.backend.global.redis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, Object value, long duration, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, duration, timeUnit);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean isKeyExists(String redisKey) {
        return redisTemplate.hasKey(redisKey);
    }
}
