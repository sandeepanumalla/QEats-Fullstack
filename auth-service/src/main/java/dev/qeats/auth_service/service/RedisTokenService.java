package dev.qeats.auth_service.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class RedisTokenService {

    private static final long TOKEN_EXPIRY_TIME = 60000 * 3; // 3 minutes
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String TOKEN_REVOKE_KEY_PREFIX = "revoked_tokens:";
    private static final String HEARTBEAT_KEY = "redis-heartbeat-listener";


    public RedisTokenService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String hashToken(String token) {
        // You can use a proper hashing algorithm here, e.g., SHA-256
        return DigestUtils.sha256Hex(token);
    }

    public void saveToken(String token) {
        String tokenHash = hashToken(token);
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("token", token);
        tokenData.put("entryTime", System.currentTimeMillis());
        redisTemplate.opsForHash().putAll(TOKEN_REVOKE_KEY_PREFIX + tokenHash, tokenData);
    }

    public boolean isTokenPresent(String token) {
        String tokenHash = hashToken(token);
        Map<Object, Object> tokenData = redisTemplate.opsForHash().entries(TOKEN_REVOKE_KEY_PREFIX + tokenHash);
        return !tokenData.isEmpty();
    }

    public Long getTokenRevocationTime(String token) {
        String tokenHash = hashToken(token);
        return (Long) redisTemplate.opsForHash().get(TOKEN_REVOKE_KEY_PREFIX + tokenHash, "revocationTime");
    }

    @Scheduled(fixedRate = TOKEN_EXPIRY_TIME)
    public void deleteTokenAfterSometime(String token) {
        Set<String> keys = redisTemplate.keys(TOKEN_REVOKE_KEY_PREFIX + "*");

        if (keys != null) {
            for (String key : keys) {
                Long revocationTime = (Long) redisTemplate.opsForHash().get(key, "revocationTime");
                // Check if the token revocation time has exceeded the expiry time
                if (revocationTime != null && isExpired(revocationTime)) {
                    // Delete the expired token from Redis
                    redisTemplate.delete(key);
                    System.out.println("Deleted expired token: " + key);
                }
            }
        }
    }

    private boolean isExpired(Long revocationTime) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - revocationTime) > TOKEN_EXPIRY_TIME;
    }

    public boolean isHeartbeatValid() {
        // Redis key for the heartbeat

        // Retrieve the heartbeat data from Redis
        Map<Object, Object> heartbeatData = redisTemplate.opsForHash().entries(HEARTBEAT_KEY);

        if (heartbeatData.isEmpty()) {
            return false; // No heartbeat found
        }

        // Extract the lastHeartbeatTime (stored as a String)
        String lastHeartbeatTimeStr = (String) heartbeatData.get("lastHeartbeatTime");

        if (lastHeartbeatTimeStr == null) {
            return false; // No lastHeartbeatTime found
        }

        // Convert the lastHeartbeatTime string to LocalDateTime
        LocalDateTime lastHeartbeatTime = LocalDateTime.parse(lastHeartbeatTimeStr);

        // Get the current time
        LocalDateTime currentTime = LocalDateTime.now();

        // Calculate the time difference in minutes
        long minutesSinceLastHeartbeat = ChronoUnit.MINUTES.between(lastHeartbeatTime, currentTime);

        // Return true if the difference is less than or equal to 5 minutes, otherwise return false
        return minutesSinceLastHeartbeat <= 5;
    }
}
