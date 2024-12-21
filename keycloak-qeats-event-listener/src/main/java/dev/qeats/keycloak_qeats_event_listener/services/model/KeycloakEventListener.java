package dev.qeats.keycloak_qeats_event_listener.services.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class KeycloakEventListener {

    private String id;

    private LocalDateTime lastHeartbeatTime;

}
