package dev.qeats.keycloak_qeats_event_listener.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.internals.Topic;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;


@Slf4j
public final class Producer {
    private static final String BOOTSTRAP_SERVER = "kafka:9092";
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS); // ObjectMapper for JSON serialization

    private static volatile KafkaProducer<String, String> instance;

    private Producer() {
    }

    public static KafkaProducer<String, String> createProducer() {
        if (instance == null) {
            synchronized (Producer.class) {
                if (instance == null) {
                    log.info("Creating Kafka Producer");
                    instance = new KafkaProducer<>(getProperties());
                }
            }
        }
        return instance;
    }

    public static void publishEvent(KafkaProducer<String, String> producer, String topic, Object event) {
        try {
            String message = objectMapper.writeValueAsString(event); // Convert the event to JSON
            ProducerRecord<String, String> eventRecord = new ProducerRecord<>(topic, message);
            producer.send(eventRecord, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Error while publishing event", exception);
                } else {
                    log.info("Event published successfully to topic: {} at offset: {}", topic, metadata.offset());
                }
            });
        } catch (Exception e) {
            log.error("Failed to serialize event", e);
        }
    }

    public static Properties getProperties() {
        log.info("Creating Kafka Producer properties");
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }
}
