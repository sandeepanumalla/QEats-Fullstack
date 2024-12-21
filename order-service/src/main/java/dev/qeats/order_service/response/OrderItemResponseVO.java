package dev.qeats.order_service.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponseVO {
    private long id;
    private String productId;
    private String productName;
    private String productDescription;
    private int quantity;
    private double price;
}
