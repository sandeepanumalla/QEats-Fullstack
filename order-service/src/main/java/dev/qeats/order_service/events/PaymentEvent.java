package dev.qeats.order_service.events;

public class PaymentEvent {
    private String orderId;
    private String paymentId;
    private double amount;
    private String status;
}
