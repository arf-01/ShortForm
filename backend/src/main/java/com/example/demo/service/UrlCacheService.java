package com.example.demo.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UrlCacheService {

    private static final String KEY_PREFIX = "url:";

    private final StringRedisTemplate redisTemplate;

    public UrlCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String get(String shortCode) {
        return redisTemplate.opsForValue().get(key(shortCode));
    }

    public void put(String shortCode, String url) {
        redisTemplate.opsForValue().set(
                key(shortCode),
                url,
                Duration.ofSeconds(60)
        );
    }

    public void delete(String shortCode) {
        redisTemplate.delete(key(shortCode));
    }

    private String key(String shortCode) {
        return KEY_PREFIX + shortCode;
    }
}