package dev.qeats.auth_service.service;

import dev.qeats.auth_service.entity.KeycloakEventListenerHeartbeat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//@ContextConfiguration(classes = AuthServiceApplication.class)
@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = TestConfig.class)
@SpringBootTest
//@OverrideAutoConfiguration(enabled = true)
//@ComponentScan(
//        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
//)
//@Import({TestConfig.class})
//@TestPropertySource(
//        properties = {
//                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
//        }
//)
//@EnableAutoConfiguration(exclude = {EurekaClientAutoConfiguration.class, SecurityAutoConfiguration.class})
@ActiveProfiles("test")  // Use the 'test' profile to load the correct Redis configuration
public class KafkaConsumerTest {

    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String listenerId = "test-listener";

    @BeforeEach
    public void setup() {
        // Clear Redis data before each test
        redisTemplate.delete(listenerId);
    }

    @Test
    public void testUpdateHeartbeat() {
        // Update heartbeat
        kafkaConsumer.updateHeartbeat(listenerId);

        // Fetch the heartbeat from Redis
//        KeycloakEventListenerHeartbeat heartbeat = (KeycloakEventListenerHeartbeat) redisTemplate.opsForValue().get(listenerId);

        // Assert that the heartbeat is not null
//        assertNotNull(heartbeat);

        // Assert that the heartbeat ID matches
//        assertEquals(listenerId, heartbeat.getId());

        // Assert that the timestamp is recent (within a reasonable range)
//        LocalDateTime now = LocalDateTime.now();
//        assertEquals(now.getYear(), heartbeat.getLastHeartbeatTime().getYear());
//        assertEquals(now.getDayOfYear(), heartbeat.getLastHeartbeatTime().getDayOfYear());
//        assertEquals(now.getHour(), heartbeat.getLastHeartbeatTime().getHour());
//        assertEquals(now.getMinute(), heartbeat.getLastHeartbeatTime().getMinute());


        String listenerId = (String) redisTemplate.opsForHash().get("redis-listener", "id");

        assertNotNull(listenerId);
        assertEquals("test-listener", listenerId);
    }

    @Test
    public void test() {
            String redisKey = "redis-listener";

            // Creating a map of field-value pairs
            Map<String, Object> listenerData = new HashMap<>();
            listenerData.put("id", "test-listener-checking");
            listenerData.put("lastHeartbeatTime", LocalDateTime.now().toString());

            // Storing all fields at once
            redisTemplate.opsForHash().putAll(redisKey, listenerData);

            // Fetching the value of a specific field
            String listenerId = (String) redisTemplate.opsForHash().get(redisKey, "id");

            // Assert that the listener ID matches
            assertEquals("test-listener-checking", listenerId);
    }


    @Test
    public void testHeartbeatTime() {
        Object heartbeatTime = redisTemplate.opsForHash().get("redis-listener", "lastHeartbeatTime");

        assertNotNull(heartbeatTime);

        LocalDateTime storedHeartbeatTime = LocalDateTime.parse((CharSequence) heartbeatTime);

        LocalDateTime currentTime = LocalDateTime.now();

        Duration duration = Duration.between(storedHeartbeatTime, currentTime);

        assertTrue(duration.getSeconds() <= 300, "The heartbeat time should be within 5 minutes of the current time");

        System.out.println(LocalDateTime.parse((CharSequence) heartbeatTime));
    }

}
