package dev.qeats.order_service.repository;

import dev.qeats.order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

//    Order findByOrderId(long orderId);

    @Query("SELECT o FROM Order o WHERE o.customerId = ?1")
    List<Order> findByUserId(String userId);

    @Query("SELECT o FROM Order o WHERE o.restaurantId IN :restaurantIds AND o.orderStatus = 'PROCESSING'")
    List<Order> findActiveOrdersByRestaurantIds(@Param("restaurantIds") List<Long> restaurantIds);
}
