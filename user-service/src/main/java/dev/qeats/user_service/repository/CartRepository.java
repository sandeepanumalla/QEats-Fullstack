package dev.qeats.user_service.repository;

import dev.qeats.user_service.model.Cart;
import dev.qeats.user_service.model.CartItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface CartRepository extends ReactiveCrudRepository<Cart, Long> {

    Mono<Cart> findByCartId(long cartId);

    Mono<Cart> findByUserId(String userId);


//    @Query(value = "Select exists(select * from cart where product_id = ?1)")
//    Mono<Boolean> existsByProductId(long productId);

}
