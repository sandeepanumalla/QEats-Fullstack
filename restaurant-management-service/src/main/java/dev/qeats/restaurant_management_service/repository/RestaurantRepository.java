package dev.qeats.restaurant_management_service.repository;

import dev.qeats.restaurant_management_service.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, JpaSpecificationExecutor<Restaurant> {

//    Optional<Restaurant> findByRestaurantName(String restaurantName);

//    @Query(value = "select * from restaurant where restaurant_id = ?1", nativeQuery = true)
//    Optional<Restaurant> findByRestaurantId(String restaurantId);

    // findByUserId
    @Query(value = "select * from restaurant where user_id = ?1", nativeQuery = true)
    List<Restaurant> findByUserId(String userId);

    // exists by user and restaurant id
    @Query(value = "select exists(select * from restaurant where user_id = ?1 and id = ?2)", nativeQuery = true)
    Long existsByUserIdAndRestaurantId(String userId, Long restaurantId);

}
