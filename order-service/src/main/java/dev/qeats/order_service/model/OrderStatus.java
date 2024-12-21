package dev.qeats.order_service.model;

public enum OrderStatus {
    PLACED("Order has been placed"),
    CONFIRMED("Order has been confirmed by the restaurant"),
    PROCESSING("Order is being prepared"),
    READY_FOR_PICKUP("Order is ready for pickup"),
    OUT_FOR_DELIVERY("Order is out for delivery"),
    DELIVERED("Order has been delivered"),
    CANCELLED("Order has been cancelled");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

