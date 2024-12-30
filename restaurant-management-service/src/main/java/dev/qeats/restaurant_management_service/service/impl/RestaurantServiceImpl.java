package dev.qeats.restaurant_management_service.service.impl;

import dev.qeats.restaurant_management_service.Specification.RestaurantSpecification;
import dev.qeats.restaurant_management_service.controller.SortBy;
import dev.qeats.restaurant_management_service.model.*;
import dev.qeats.restaurant_management_service.repository.AddressRepository;
import dev.qeats.restaurant_management_service.repository.BranchRepository;
import dev.qeats.restaurant_management_service.repository.RestaurantRepository;
import dev.qeats.restaurant_management_service.responseVO.BranchVO;
import dev.qeats.restaurant_management_service.responseVO.MenuItemsVO;
import dev.qeats.restaurant_management_service.responseVO.RestaurantVO;
import dev.qeats.restaurant_management_service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static dev.qeats.restaurant_management_service.controller.SortBy.FASTEST_DELIVERY;
import static dev.qeats.restaurant_management_service.controller.SortBy.NEAREST;

@Repository("restaurantService")
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final AddressRepository addressRepository;
    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;
    private final BranchRepository branchRepository;
    private final BranchServiceImpl branchService;
    private final CuisineServiceImpl cuisineService;


    @Override
    @Transactional
    public RestaurantVO updateRestaurantDetails(Long restaurantId, RestaurantVO restaurantVO) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);
        if (restaurantOptional.isEmpty()) {
            throw new RuntimeException("Restaurant not found with ID: " + restaurantId);
        }

        Restaurant restaurant = restaurantOptional.get();

        // Update basic fields
        restaurant.setName(restaurantVO.getName());
        restaurant.setDescription(restaurantVO.getDescription());
        restaurant.setRating(restaurantVO.getRating());
        restaurant.setDeliveryFee(restaurantVO.getDeliveryFee());
        restaurant.setDeliveryTime(restaurantVO.getDeliveryTime());
        restaurant.setUserId(restaurantVO.getUserId());

        // Replace branches and handle addresses
        List<Branch> newBranches = modelMapper.map(restaurantVO.getBranches(), new TypeToken<List<Branch>>() {}.getType());
        if (newBranches == null) {
            newBranches = new ArrayList<>();
        }
        for (Branch branch : newBranches) {
            // Maintain bidirectional relationship
            branch.setRestaurant(restaurant);

            // Handle Address
            Address address = branch.getAddress();
            if (address != null) {
                if (address.getId() != 0) {
                    // Existing address, fetch and update
                    Address existingAddress = addressRepository.findById(address.getId())
                            .orElseThrow(() -> new RuntimeException("Address not found with ID: " + address.getId()));
//                    existingAddress.setId(0L);
                    existingAddress.setStreet(address.getStreet());
                    existingAddress.setCity(address.getCity());
                    existingAddress.setState(address.getState());
                    existingAddress.setZipCode(address.getZipCode());
                    branch.setAddress(existingAddress);
                } else {
                    // New address, save it
                    addressRepository.save(address);
                    branch.setAddress(address);
                }
            }
        }
        restaurant.getBranches().clear();
        restaurant.getBranches().addAll(newBranches);

        // Replace cuisines
        List<Cuisine> newCuisines = modelMapper.map(restaurantVO.getCuisines(), new TypeToken<List<Cuisine>>() {}.getType());
        if (newCuisines == null) {
            newCuisines = new ArrayList<>();
        }
        for (Cuisine cuisine : newCuisines) {
            cuisine.setRestaurant(restaurant); // Maintain bidirectional relationship
        }
        restaurant.getCuisines().clear();
        restaurant.getCuisines().addAll(newCuisines);

        // Replace menu items
        List<MenuItem> newMenuItems = modelMapper.map(restaurantVO.getMenuItems(), new TypeToken<List<MenuItem>>() {}.getType());
        if (newMenuItems == null) {
            newMenuItems = new ArrayList<>();
        }
        for (MenuItem menuItem : newMenuItems) {
            menuItem.setRestaurant(restaurant); // Maintain bidirectional relationship
        }
        restaurant.getMenuItems().clear();
        restaurant.getMenuItems().addAll(newMenuItems);

        // Save the updated restaurant
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);

        // Convert updated entity back to RestaurantVO and return it
        return updatedRestaurant.toRestaurantVO();
    }


//    @Override
//    public RestaurantVO updateRestaurantDetails(Long restaurantId, RestaurantVO restaurantVO) {
//        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);
//        if (restaurantOptional.isEmpty()) {
//            throw new RuntimeException("Restaurant not found with ID: " + restaurantId);
//        }
//
//        Restaurant restaurant = restaurantOptional.get();
//        // Update the restaurant entity with new details
//        restaurant.setName(restaurantVO.getName());
//        restaurant.setDescription(restaurantVO.getDescription());
//        restaurant.setRating(restaurantVO.getRating());
//        restaurant.setDeliveryFee(restaurantVO.getDeliveryFee());
//        restaurant.setDeliveryTime(restaurantVO.getDeliveryTime());
//        restaurant.setCuisines(restaurantVO.toRestaurant().getCuisines());
//        restaurant.setMenuItems(restaurantVO.toRestaurant().getMenuItems());
//        restaurant.setBranches(restaurantVO.toRestaurant().getBranches());
//        // Update other fields if necessary, e.g. deliveryFee, rating, etc.
//
//        // Save the updated restaurant to the database
//        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
//
//        // Convert updated entity back to RestaurantVO and return it
//        return updatedRestaurant.toRestaurantVO();
//    }

    @Override
    public void deleteRestaurant(Long restaurantId, String userId) throws Exception {
        Long exists = restaurantRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
        if(exists != 1L) {
            throw new Exception("only restaurant can delete");
        }
        restaurantRepository.deleteById(restaurantId);
    }

    @Override
    public RestaurantVO addRestaurant(RestaurantVO restaurantVO) {
        Restaurant restaurant = modelMapper.map(restaurantVO, Restaurant.class);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        addMenuToRestaurant(savedRestaurant.getId(), restaurantVO.getMenuItems());
        restaurantVO.getCuisines().forEach(cuisine -> cuisineService.addCuisineToRestaurant(savedRestaurant.getId(), cuisine));
        restaurantVO.getBranches().forEach(branch -> branchService.addBranch(savedRestaurant.getId(), branch));
        return modelMapper.map(savedRestaurant, RestaurantVO.class);
    }



    @Override
    public RestaurantVO getRestaurant(Long restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isPresent()) {
            return modelMapper.map(restaurant, RestaurantVO.class);
        }
        return null;
    }

    @Override
    public List<RestaurantVO> searchRestaurants(String city, String restaurantName, String cuisineName, SortBy sortBy, double userLat, double userLng) {
        Specification<Restaurant> spec = Specification
                .where(null);

        // Add filters dynamically if they are not null or empty
        if (city != null && !city.isEmpty()) {
            spec = spec.and(RestaurantSpecification.hasCity(city));
        }
        if (restaurantName != null && !restaurantName.isEmpty()) {
            spec = spec.and(RestaurantSpecification.hasRestaurantName(restaurantName));
        }
        if (cuisineName != null && !cuisineName.isEmpty()) {
            spec = spec.and(RestaurantSpecification.hasCuisine(cuisineName));
        }

        List<RestaurantVO> restaurants = new ArrayList<>(restaurantRepository
                .findAll(spec).stream().map(restaurant -> {
                    RestaurantVO restaurantVO = restaurant.toRestaurantVO();

                    // Calculate the distance for each restaurant's branch
                    double minDistance = Double.MAX_VALUE;
                    if(userLat != 0.0 && userLng != 0.0) {
                        for (Branch branch : restaurant.getBranches()) {
                            double distance = calculateDistance(userLat, userLng, branch.getAddress().getLatitude(), branch.getAddress().getLongitude());
                            if (distance < minDistance) {
                                minDistance = distance;
                            }
                        }
                        restaurantVO.setDistance(minDistance);  // Set the minimum distance
                    }

                    return restaurantVO;
                }).toList());


        // Apply sorting logic based on the sortBy enum
        switch (sortBy) {
            case NEAREST:
                restaurants.sort(Comparator.comparing(RestaurantVO::getDistance)); // Assuming you have a distance field
                break;
            case FASTEST_DELIVERY:
                restaurants.sort(Comparator.comparing(RestaurantVO::getDeliveryTime));
                break;
            case TOP_RATED:
                restaurants.sort(Comparator.comparing(RestaurantVO::getRating).reversed());
                break;
            case LOW_DELIVERY_FEE:
                restaurants.sort(Comparator.comparing(RestaurantVO::getDeliveryFee));
                break;
            case BEST_MATCH:
            default:
                // Default sorting (Best Match or custom logic)
                break;
        }

        return restaurants;
    }

    public double calculateDistance(double userLat, double userLng, double branchLat, double branchLng) {
        final int EARTH_RADIUS = 6371; // Radius of the earth in kilometers
        double latDistance = Math.toRadians(branchLat - userLat);
        double lonDistance = Math.toRadians(branchLng - userLng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(branchLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c; // Distance in kilometers
    }


//    @Override
//    public RestaurantVO getRestaurantByRestaurantName(String restaurantName) {
//        Optional<Restaurant> restaurant = restaurantRepository.findByRestaurantName(restaurantName);
//        return restaurant.map(value -> modelMapper.map(value, RestaurantVO.class)).orElse(null);
//    }
//
//    @Override
//    public List<RestaurantVO> getRestaurantsByLocation() {
//        return List.of();
//    }

    @Override
    public List<RestaurantVO> getRestaurants() {
        return List.of();
    }

    @Override
    public List<MenuItemsVO> addMenuToRestaurant(long restaurantId, List<MenuItemsVO> menuItemsVO) {
        // Fetch the restaurant by ID
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant with ID " + restaurantId + " not found"));

        // Map MenuItemsVO to MenuItem entities
        List<MenuItem> menuItems = menuItemsVO.stream()
                .map(menuItemVO -> {
                    MenuItem menuItem = new MenuItem();
                    menuItem.setName(menuItemVO.getName());
                    menuItem.setPrice(menuItemVO.getPrice());
                    menuItem.setNonVeg(menuItemVO.isNonVeg());
                    menuItem.setRestaurant(restaurant); // Set the restaurant relationship
                    return menuItem;
                })
                .toList();

        // Add the menu items to the restaurant's menu
        restaurant.getMenuItems().addAll(menuItems);

        // Save the updated restaurant
        restaurantRepository.save(restaurant);

        // Map the saved menu items back to MenuItemsVO and return
        return menuItems.stream()
                .map(menuItem -> {
                    MenuItemsVO menuItemVO = new MenuItemsVO();
//                    menuItemVO.setId(menuItem.getId());
                    menuItemVO.setName(menuItem.getName());
                    menuItemVO.setPrice(menuItem.getPrice());
                    menuItemVO.setNonVeg(menuItem.isNonVeg());
                    return menuItemVO;
                })
                .toList();
    }

    @Override
    public List<RestaurantVO> getUserRestaurant(String userId) {
        List<Restaurant> restaurants = restaurantRepository.findByUserId(userId);
        return modelMapper.map(restaurants, new TypeToken<List<RestaurantVO>>(){}.getType());
    }


//
//    @Override
//    public void getRestaurantByCuisine(String cuisine) {
//
//    }
}
