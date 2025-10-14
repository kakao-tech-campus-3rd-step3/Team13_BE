package com.b4f2.pting.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class InMemoryCache {

    private final Map<String, CacheValue> cache = new ConcurrentHashMap<>();

    private static class CacheValue {

        String value;
        long expireTime;

        CacheValue(String value, long ttlMillis) {
            this.value = value;
            this.expireTime = System.currentTimeMillis() + ttlMillis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    public void set(String key, String value, long ttlMillis) {
        cache.put(key, new CacheValue(value, ttlMillis));
    }

    public String get(String key) {
        CacheValue value = cache.get(key);
        if (value == null || value.isExpired()) {
            cache.remove(key);
            return null;
        }
        return value.value;
    }

    public void delete(String key) {
        cache.remove(key);
    }
}
