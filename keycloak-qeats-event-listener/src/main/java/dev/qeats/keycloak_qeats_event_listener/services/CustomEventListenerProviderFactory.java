package dev.qeats.keycloak_qeats_event_listener.services;

import dev.qeats.keycloak_qeats_event_listener.services.model.KeycloakEventListener;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.*;


public class CustomEventListenerProviderFactory implements EventListenerProviderFactory {


    private static final Logger log = LoggerFactory.getLogger(CustomEventListenerProviderFactory.class);

    ScheduledExecutorService executor = null;

    private KafkaProducer<String, String> producer;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        log.info("Creating CustomEventListenerProvider");
        return new CustomEventListenerProvider(producer);
    }

    @Override
    public void init(Config.Scope scope) {
        producer = Producer.createProducer();
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            log.info("Sending Heartbeat event to Kafka");
            KeycloakEventListener listener = new KeycloakEventListener();
            listener.setId("keycloak-qeats-heartbeat-listener-event");
            listener.setLastHeartbeatTime(LocalDateTime.now());
            Producer.publishEvent(producer, "keycloak-heartbeat-events", listener);
            log.info("Heartbeat event sent to Kafka");
        }, 0, 5, TimeUnit.MINUTES);
        log.info("Initializing CustomEventListenerProvider");
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        log.info("Post initializing CustomEventListenerProvider");
    }

    @Override
    public void close() {
        log.info("Closing CustomEventListenerProviderFactory");

        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        log.error("Executor did not terminate");
                    }
                    log.info("Executor terminated");
                }
            } catch (InterruptedException ie) {
                executor.shutdownNow();
                log.error("Executor interrupted");
                Thread.currentThread().interrupt();
            }
        }

        // Close the Kafka producer

        if (producer != null) {
            log.info("Closing Kafka Producer");
            producer.close();
        }

    }

    @Override
    public String getId() {
        return "keycloak-qeats-event-listener";
    }
}
