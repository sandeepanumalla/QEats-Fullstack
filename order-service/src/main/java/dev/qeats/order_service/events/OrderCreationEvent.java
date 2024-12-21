package dev.qeats.order_service.events;

import java.util.List;

public class OrderCreationEvent {
    private String orderId;
    private String customerId;
    private List<String> productIds;
    private double totalAmount;


}
