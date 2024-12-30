package dev.qeats.restaurant_management_service.repository;

import dev.qeats.restaurant_management_service.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RestaurantRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void testFindByUserId() {
        String userId = "";
        List<Restaurant> optionalRestaurant = restaurantRepository.findByUserId("77055c49-2dfe-4aec-8f15-406a09289e34");

        System.out.println(optionalRestaurant.getFirst());
//        assertTrue(optionalRestaurant.isPresent(), "Restaurant should be found");
//
//        assertEquals("", optionalRestaurant.get().getName(), "Restaurant name should match");
    }

    @Test
    public void testExistByRestaurantIdAndUserId() {
        Long ans = restaurantRepository.existsByUserIdAndRestaurantId("77055c49-2dfe-4aec-8f15-406a09289e34", 11L);
        assertEquals(1L, ans);
    }

}


