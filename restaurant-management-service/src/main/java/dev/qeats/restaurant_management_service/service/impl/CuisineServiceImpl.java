package dev.qeats.restaurant_management_service.service.impl;

import dev.qeats.restaurant_management_service.model.Cuisine;
import dev.qeats.restaurant_management_service.model.Restaurant;
import dev.qeats.restaurant_management_service.repository.CuisineRepository;
import dev.qeats.restaurant_management_service.repository.RestaurantRepository;
import dev.qeats.restaurant_management_service.responseVO.CuisineVO;
import dev.qeats.restaurant_management_service.service.CuisineService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("cuisineService")
public class CuisineServiceImpl implements CuisineService {

    private final CuisineRepository cuisineRepository;

    private final RestaurantRepository restaurantRepository;

    public CuisineServiceImpl(CuisineRepository cuisineRepository, RestaurantRepository restaurantRepository) {
        this.cuisineRepository = cuisineRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public CuisineVO addCuisineToRestaurant(Long restaurantId, CuisineVO cuisineVO) {

        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);
        if (restaurantOptional.isEmpty()) {
            throw new RuntimeException("Restaurant not found with ID: " + restaurantId);
        }

        Restaurant restaurant = restaurantOptional.get();

        Cuisine cuisine = new Cuisine();
        cuisine.setName(cuisineVO.getName());
        cuisine.setDescription(cuisineVO.getDescription());
        cuisine.setRestaurant(restaurant);

        Cuisine savedCuisine = cuisineRepository.save(cuisine);

        return savedCuisine.toCuisineVO();
    }

    @Override
    public CuisineVO updateCuisineForRestaurant(Long restaurantId, Long cuisineId, CuisineVO cuisineVO) {
        // fetch restaurant
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();

        return null;
    }
    @Override
    public List<CuisineVO> getAllCuisines() {
        // Fetch all cuisines from the repository
        List<Cuisine> cuisines = cuisineRepository.findDistinctCuisineNames();
        System.out.println("bal" + cuisines.size());

        // Map Cuisine entities to CuisineVO objects and filter distinct cuisines
        return cuisines.stream()
                .map(Cuisine::toCuisineVO)
                .distinct() // Ensure distinct results based on CuisineVO
                .toList();
    }

}
