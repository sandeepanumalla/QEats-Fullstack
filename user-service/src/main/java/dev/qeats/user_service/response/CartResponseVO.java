package dev.qeats.user_service.response;

import dev.qeats.user_service.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartResponseVO {
    private long cartId;
    private User user;
    private List<CartItemResponseVO> items;
    private double totalCost = 0.0;
}
