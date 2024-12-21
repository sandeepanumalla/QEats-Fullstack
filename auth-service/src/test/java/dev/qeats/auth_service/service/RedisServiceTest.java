package dev.qeats.auth_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class RedisServiceTest {

    @Autowired
    private RedisTokenService redisTokenService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String HEARTBEAT_KEY = "redis-heartbeat-listener";

    private static final String TOKEN_REVOKE_KEY_PREFIX = "revoked_tokens:";



    @Test
    void testSaveAndRetrieveToken() {
        String token = "testToken";

        // Save the token using the RedisTokenService
        redisTokenService.saveToken(token);

        // Verify that the token is stored in Redis
        Assertions.assertTrue(redisTokenService.isTokenPresent(token));

        // Retrieve token data directly from Redis
        String tokenHash = redisTokenService.hashToken(token);
        Map<Object, Object> tokenData = redisTemplate.opsForHash().entries("revoked_tokens:" + tokenHash);

        assertNotNull(tokenData);
        assertEquals(token, tokenData.get("token"));
        assertNotNull(tokenData.get("entryTime"));
    }

    @Test
    public void testIsHeartbeatValid_whenHeartbeatIsFresh() {
        // Prepare test data (heartbeat time is within the last 5 minutes)
        Map<String, Object> heartbeatData = new HashMap<>();
        heartbeatData.put("lastHeartbeatTime", LocalDateTime.now().minusMinutes(3).toString());
        redisTemplate.opsForHash().putAll(HEARTBEAT_KEY, heartbeatData);

        // Call the method
        boolean result = redisTokenService.isHeartbeatValid();

        // Assert the result
        assertTrue( "Heartbeat should be valid (within 5 minutes).", result);
    }

    @Test
    void testSaveAndFetchRevokedToken() {
        String token = "revokedTokenExample";

        // Save the token using RedisTokenService
        redisTokenService.saveToken(token);

        // Check if the token is present
        assertTrue( "Token should be present in revoked tokens.", redisTokenService.isTokenPresent(token));

        // Retrieve the token data directly from Redis
        String tokenHash = redisTokenService.hashToken(token);
        Map<Object, Object> tokenData = redisTemplate.opsForHash().entries(TOKEN_REVOKE_KEY_PREFIX + tokenHash);

        // Assertions to check token is stored properly
        assertNotNull(tokenData, "Token data should not be null.");
        assertEquals(token, tokenData.get("token"), "Stored token should match the input token.");
        assertNotNull(tokenData.get("entryTime"), "Entry time should not be null.");
    }



}
