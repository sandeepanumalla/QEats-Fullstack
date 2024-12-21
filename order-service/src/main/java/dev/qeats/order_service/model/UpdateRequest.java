package dev.qeats.order_service.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UpdateRequest {
    private String userId; // Unique user identifier
    private String message;
    private Map<String, String> additionalData;
}
