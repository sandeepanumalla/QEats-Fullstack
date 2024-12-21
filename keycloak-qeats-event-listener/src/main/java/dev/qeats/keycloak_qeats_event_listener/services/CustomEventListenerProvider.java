package dev.qeats.keycloak_qeats_event_listener.services;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.keycloak.events.EventType.*;


@Slf4j
@NoArgsConstructor(force = true)
public class CustomEventListenerProvider implements EventListenerProvider {

    private static final Logger logger = LoggerFactory.getLogger(CustomEventListenerProvider.class);
    private final KafkaProducer<String, String> producer;

    List<EventType> nonAdminEventTypes = Arrays.asList(LOGIN, LOGOUT, REGISTER, LOGIN_ERROR, UPDATE_PASSWORD, DELETE_ACCOUNT);

    public CustomEventListenerProvider(KafkaProducer<String, String> producer) {
        log.info("inside CustomEventListenerProvider constructor");
        this.producer = producer;  // Create the producer once
    }

    @Override
    public void onEvent(Event event) {
        log.info("Caught user event {}", event.toString());
        log.info("Caught user event {}", event);
        if (nonAdminEventTypes.contains(event.getType())) {
            logger.info("User Event: {} {}", event.getType(), event.getUserId());
            // send the event to Kafka
            logger.info("Sending event to Kafka");
            Producer.publishEvent(producer, "keycloak-user-events", event);
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        log.info("Caught admin event {}", adminEvent);
        if (adminEvent.getOperationType().equals(OperationType.DELETE)) {
            logger.info("User Deletion Event: {}", adminEvent.getResourcePath());
            // Handle user deletion event
            Producer.publishEvent(producer, "keycloak-user-events", adminEvent.toString());
        }
    }

    @Override
    public void close() {

    }
}
