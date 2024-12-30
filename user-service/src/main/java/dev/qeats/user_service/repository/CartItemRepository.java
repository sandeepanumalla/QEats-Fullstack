package dev.qeats.user_service.repository;

import dev.qeats.user_service.model.CartItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {
    @Query("SELECT * FROM cart_items WHERE cart_id = :cartId and restaurant_id = :restaurantId")
    Flux<CartItem> findByCartIdAndRestaurantId(long cartId, String restaurantId);
}
