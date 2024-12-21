package dev.qeats.user_service.repository;

import dev.qeats.user_service.model.Cart;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CartRepository extends ReactiveCrudRepository<Cart, Long> {
    Mono<Cart> findByCartId(long cartId);

//    @Query(value = "Select exists(select * from cart where product_id = ?1)")
//    Mono<Boolean> existsByProductId(long productId);

}
