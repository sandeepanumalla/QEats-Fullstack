package dev.qeats.order_service.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OrderItemVO {
    private String productName;
    private String productId; // menuId
    private String description;
    private int quantity;
    private double price;
    private String currency;
}
