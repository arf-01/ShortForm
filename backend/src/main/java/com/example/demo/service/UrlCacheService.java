package com.example.demo.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UrlCacheService {

    private static final String KEY_PREFIX = "url:";
    private static final Logger logger = LoggerFactory.getLogger(UrlCacheService.class);

    private final StringRedisTemplate redisTemplate;
    private final boolean enabled;

    public UrlCacheService(
            StringRedisTemplate redisTemplate,
            @Value("${app.redis.enabled:true}") boolean enabled) {
        this.redisTemplate = redisTemplate;
        this.enabled = enabled;
    }

    public String get(String shortCode) {
        if (!enabled) {
            return null;
        }
        try {
            return redisTemplate.opsForValue().get(key(shortCode));
        } catch (DataAccessException exception) {
            logger.warn("Redis read failed for short code {}", shortCode, exception);
            return null;
        }
    }

    public void put(String shortCode, String url) {
        if (!enabled) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(
                    key(shortCode),
                    url,
                    Duration.ofSeconds(60)
            );
        } catch (DataAccessException exception) {
            logger.warn("Redis write failed for short code {}", shortCode, exception);
        }
    }

    public void delete(String shortCode) {
        if (!enabled) {
            return;
        }
        try {
            redisTemplate.delete(key(shortCode));
        } catch (DataAccessException exception) {
            logger.warn("Redis delete failed for short code {}", shortCode, exception);
        }
    }

    private String key(String shortCode) {
        return KEY_PREFIX + shortCode;
    }
}