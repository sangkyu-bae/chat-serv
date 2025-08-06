package com.chat.user.module;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    // ==================== String 타입 ====================
    
    /**
     * String 타입 저장 (TTL 없음)
     */
    public void setString(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("String saved: key={}, value={}", key, value);
        } catch (Exception e) {
            log.error("Failed to save string: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("Redis string save failed", e);
        }
    }

    /**
     * String 타입 저장 (TTL 설정)
     */
    public void setString(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.debug("String saved with TTL: key={}, value={}, timeout={} {}", key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Failed to save string with TTL: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("Redis string save with TTL failed", e);
        }
    }

    /**
     * String 타입 저장 (Duration 사용)
     */
    public void setString(String key, Object value, Duration duration) {
        try {
            redisTemplate.opsForValue().set(key, value, duration);
            log.debug("String saved with Duration: key={}, value={}, duration={}", key, value, duration);
        } catch (Exception e) {
            log.error("Failed to save string with Duration: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("Redis string save with Duration failed", e);
        }
    }

    /**
     * String 타입 조회
     */
    public Object getString(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            log.debug("String retrieved: key={}, value={}", key, value);
            return value;
        } catch (Exception e) {
            log.error("Failed to get string: key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    /**
     * String 타입 삭제
     */
    public void deleteString(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("String deleted: key={}", key);
        } catch (Exception e) {
            log.error("Failed to delete string: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("Redis string delete failed", e);
        }
    }

    /**
     * 키 존재 여부 확인
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Failed to check key existence: key={}, error={}", key, e.getMessage());
            return false;
        }
    }

    /**
     * TTL 조회
     */
    public Long getExpire(String key, TimeUnit unit) {
        try {
            return redisTemplate.getExpire(key, unit);
        } catch (Exception e) {
            log.error("Failed to get TTL: key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    // ==================== Set 타입 ====================
    
    /**
     * Set 타입 저장
     */
    public void setTypeSave(String key, String value) {
        try {
            redisTemplate.opsForSet().add(key, value);
            log.debug("Set value added: key={}, value={}", key, value);
        } catch (Exception e) {
            log.error("Failed to add set value: key={}, value={}, error={}", key, value, e.getMessage());
            throw new RuntimeException("Redis set add failed", e);
        }
    }

    /**
     * Set 타입 저장 (TTL 설정)
     */
    public void setTypeSave(String key, String value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForSet().add(key, value);
            redisTemplate.expire(key, timeout, unit);
            log.debug("Set value added with TTL: key={}, value={}, timeout={} {}", key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Failed to add set value with TTL: key={}, value={}, error={}", key, value, e.getMessage());
            throw new RuntimeException("Redis set add with TTL failed", e);
        }
    }

    /**
     * Set 타입 전체 조회
     */
    public Set<Object> findWithSetTypeByAll(String key) {
        try {
            Set<Object> members = redisTemplate.opsForSet().members(key);
            log.debug("Set members retrieved: key={}, count={}", key, members != null ? members.size() : 0);
            return members;
        } catch (Exception e) {
            log.error("Failed to get set members: key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    /**
     * Set에서 특정 값 존재 여부 확인
     */
    public boolean existsInSet(String key, String value) {
        try {
            boolean exists = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
            log.debug("Set member check: key={}, value={}, exists={}", key, value, exists);
            return exists;
        } catch (Exception e) {
            log.error("Failed to check set member: key={}, value={}, error={}", key, value, e.getMessage());
            return false;
        }
    }

    /**
     * Set에서 값 제거
     */
    public void removeFromSet(String key, String value) {
        try {
            redisTemplate.opsForSet().remove(key, value);
            log.debug("Set value removed: key={}, value={}", key, value);
        } catch (Exception e) {
            log.error("Failed to remove set value: key={}, value={}, error={}", key, value, e.getMessage());
            throw new RuntimeException("Redis set remove failed", e);
        }
    }

    // ==================== List 타입 ====================
    
    /**
     * List 타입 저장 (오른쪽에 추가)
     */
    public void listTypeSave(String key, String value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            log.debug("List value added: key={}, value={}", key, value);
        } catch (Exception e) {
            log.error("Failed to add list value: key={}, value={}, error={}", key, value, e.getMessage());
            throw new RuntimeException("Redis list add failed", e);
        }
    }

    /**
     * List 타입 저장 (TTL 설정)
     */
    public void listTypeSave(String key, String value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            redisTemplate.expire(key, timeout, unit);
            log.debug("List value added with TTL: key={}, value={}, timeout={} {}", key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Failed to add list value with TTL: key={}, value={}, error={}", key, value, e.getMessage());
            throw new RuntimeException("Redis list add with TTL failed", e);
        }
    }

    /**
     * List 타입 범위 조회
     */
    public List<Object> findWithListType(String key, long start, long end) {
        try {
            List<Object> range = redisTemplate.opsForList().range(key, start, end);
            log.debug("List range retrieved: key={}, start={}, end={}, count={}", key, start, end, range != null ? range.size() : 0);
            return range;
        } catch (Exception e) {
            log.error("Failed to get list range: key={}, start={}, end={}, error={}", key, start, end, e.getMessage());
            return null;
        }
    }

    /**
     * List 타입 전체 조회
     */
    public List<Object> findWithListTypeByAll(String key) {
        try {
            return findWithListType(key, 0, -1);
        } catch (Exception e) {
            log.error("Failed to get all list values: key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    /**
     * List에서 값 제거
     */
    public void removeFromList(String key, String value) {
        try {
            redisTemplate.opsForList().remove(key, 0, value);
            log.debug("List value removed: key={}, value={}", key, value);
        } catch (Exception e) {
            log.error("Failed to remove list value: key={}, value={}, error={}", key, value, e.getMessage());
            throw new RuntimeException("Redis list remove failed", e);
        }
    }

    // ==================== Hash 타입 ====================
    
    /**
     * Hash 타입 저장
     */
    public void hashSave(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            log.debug("Hash value saved: key={}, hashKey={}, value={}", key, hashKey, value);
        } catch (Exception e) {
            log.error("Failed to save hash value: key={}, hashKey={}, error={}", key, hashKey, e.getMessage());
            throw new RuntimeException("Redis hash save failed", e);
        }
    }

    /**
     * Hash 타입 조회
     */
    public Object findByHash(String key, String subKey) {
        try {
            Object value = redisTemplate.opsForHash().get(key, subKey);
            log.debug("Hash value retrieved: key={}, subKey={}, value={}", key, subKey, value);
            return value;
        } catch (Exception e) {
            log.error("Failed to get hash value: key={}, subKey={}, error={}", key, subKey, e.getMessage());
            return null;
        }
    }

    /**
     * Hash에서 값 제거
     */
    public void removeFromHash(String key, String hashKey) {
        try {
            redisTemplate.opsForHash().delete(key, hashKey);
            log.debug("Hash value removed: key={}, hashKey={}", key, hashKey);
        } catch (Exception e) {
            log.error("Failed to remove hash value: key={}, hashKey={}, error={}", key, hashKey, e.getMessage());
            throw new RuntimeException("Redis hash remove failed", e);
        }
    }

    // ==================== 유틸리티 메서드 ====================
    
    /**
     * 키 패턴으로 삭제
     */
    public void deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Keys deleted by pattern: pattern={}, count={}", pattern, keys.size());
            }
        } catch (Exception e) {
            log.error("Failed to delete keys by pattern: pattern={}, error={}", pattern, e.getMessage());
            throw new RuntimeException("Redis pattern delete failed", e);
        }
    }

    /**
     * Redis 연결 상태 확인
     */
    public boolean ping() {
        try {
            String result = redisTemplate.getConnectionFactory().getConnection().ping();
            return "PONG".equals(result);
        } catch (Exception e) {
            log.error("Redis ping failed: error={}", e.getMessage());
            return false;
        }
    }
} 