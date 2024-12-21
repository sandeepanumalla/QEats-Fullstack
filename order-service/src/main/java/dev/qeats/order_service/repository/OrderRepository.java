package dev.qeats.order_service.repository;

import dev.qeats.order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByOrderId(long orderId);

    @Query("SELECT o FROM Order o WHERE o.restaurantId = ?1")
    Order findByRestaurantId(String restaurantId);
}
