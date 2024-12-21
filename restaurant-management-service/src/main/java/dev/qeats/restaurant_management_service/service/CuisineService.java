package dev.qeats.restaurant_management_service.service;

import dev.qeats.restaurant_management_service.model.Cuisine;
import dev.qeats.restaurant_management_service.responseVO.CuisineVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CuisineService {

    CuisineVO addCuisineToRestaurant(Long restaurantId, CuisineVO cuisineVO);

    CuisineVO updateCuisineForRestaurant(Long restaurantId, Long cuisineId, CuisineVO cuisineVO);

    // fetchAllDistinctCuisinesFrom all restaurants
    List<CuisineVO> getAllCuisines();



}
