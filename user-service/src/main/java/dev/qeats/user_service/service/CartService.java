package dev.qeats.user_service.service;

import dev.qeats.user_service.model.Cart;
import dev.qeats.user_service.model.CartItem;
import dev.qeats.user_service.request.CartRequestVO;
import dev.qeats.user_service.response.CartItemResponseVO;
import dev.qeats.user_service.response.CartResponseVO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CartService {

    Mono<CartResponseVO> getUserCart(String userId, String restaurantId) throws Exception;

    Mono<Void> addToCart(String userId, List<CartItem> cartItems);

    Mono<Void> removeFromCart(String userId, long cartItemId, int quantity);

    Mono<Void> clearCart(String userId, String restaurantId);

    Mono<Void> checkout(String userId, String restaurantId);

    public Mono<Void> createCartForUser(String userId);

    Mono<ResponseEntity<String>> updateUserCart(String userId, String restaurantId, CartRequestVO cartRequestVO);
}
