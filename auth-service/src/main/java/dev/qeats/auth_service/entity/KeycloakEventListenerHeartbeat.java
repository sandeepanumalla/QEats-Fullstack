package dev.qeats.auth_service.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Setter
@Getter
@RedisHash("KeycloakEventListenerHeartbeat")
public class KeycloakEventListenerHeartbeat {

    @Id
    private String id;  // Unique identifier, can be the listener name or service ID

    @Indexed
    private LocalDateTime lastHeartbeatTime;  // Timestamp of the last heartbeat

    public KeycloakEventListenerHeartbeat() {
    }

    public KeycloakEventListenerHeartbeat(String id, LocalDateTime lastHeartbeatTime) {
        this.id = id;
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    @Override
    public String toString() {
        return "KeycloakEventListenerHeartbeat{" +
                "id='" + id + '\'' +
                ", lastHeartbeatTime=" + lastHeartbeatTime +
                '}';
    }

}
