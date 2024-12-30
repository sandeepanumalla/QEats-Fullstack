package dev.qeats.user_service.request;

import dev.qeats.user_service.response.CartItemResponseVO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartRequestVO {
    private long cartId;
    private String user;
    private List<CartItemRequestVO> items;
    private double totalCost = 0.0;
}
