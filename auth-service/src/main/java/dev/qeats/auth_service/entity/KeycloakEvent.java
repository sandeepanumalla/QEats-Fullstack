package dev.qeats.auth_service.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Map;

@RedisHash("KeycloakEvent")
@Getter
@Setter
public class KeycloakEvent {

    @Id
    private String id;

    private long time;

    @Indexed
    private EventType eventType;

    private String realmId;

    private String realmName;

    private String clientId;

    private String userId;

    private String sessionId;

    private String ipAddress;

    private String error;

    private Map<String, String> details;
}
