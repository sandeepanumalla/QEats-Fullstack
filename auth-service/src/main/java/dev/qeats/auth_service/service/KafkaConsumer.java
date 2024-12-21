package dev.qeats.auth_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.qeats.auth_service.entity.KeycloakEventListenerHeartbeat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

@Service
@Slf4j
public class KafkaConsumer {

    private final RedisTemplate<String, Object> redisTemplate;



    public KafkaConsumer(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

//    @KafkaListener(topics = "keycloak-heartbeat-events")
    public void consume(String message) {
        System.out.println("Received message: " + message);
    }

    @KafkaListener(topics = "keycloak-heartbeat-events")
    public void updateHeartbeat(String message)  {
        System.out.println("Received message: " + message);

        ObjectMapper objectMapper = new ObjectMapper();
        // Register the JavaTimeModule for LocalDateTime support
        objectMapper.registerModule(new JavaTimeModule());

        KeycloakEventListenerHeartbeat heartbeat = null;
        try {
            heartbeat = objectMapper.readValue(message, KeycloakEventListenerHeartbeat.class);
        } catch (Exception e) {
            log.error("Error deserializing message: ", e);
            return;
        }

        if (heartbeat != null) {
            Map<String, Object> heartbeatMap = new HashMap<>();
            heartbeatMap.put("id", heartbeat.getId());
            heartbeatMap.put("lastHeartbeatTime", heartbeat.getLastHeartbeatTime().toString());  // Converting LocalDateTime to String
            redisTemplate.opsForHash().putAll("redis-heartbeat-listener", heartbeatMap);
        }
    }

    @KafkaListener(topics = "keycloak-user-events")
    public void consumeUserEvent(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        KeycloakUserEvent userEvent = null;

        try {
            userEvent = objectMapper.readValue(message, KeycloakUserEvent.class);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing user event: ", e);
            return;
        }

        if (userEvent != null && userEvent.getTime() != null) {
            Long eventTimeMillis = userEvent.getTime();
            LocalDateTime eventTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(eventTimeMillis), ZoneId.systemDefault());

            String username = "Unknown"; // Default value if details or username is missing

            if (userEvent.getDetails() != null && userEvent.getDetails().getUsername() != null) {
                username = userEvent.getDetails().getUsername();
            }

            log.info("Processed user event: type = {}, userId = {}, eventTime = {}, username = {}",
                    userEvent.getType(),
                    userEvent.getUserId(),
                    eventTime,
                    username);
        }
    }

}
