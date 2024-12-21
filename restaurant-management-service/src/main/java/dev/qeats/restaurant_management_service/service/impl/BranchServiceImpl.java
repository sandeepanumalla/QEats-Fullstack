package dev.qeats.restaurant_management_service.service.impl;

import dev.qeats.restaurant_management_service.model.Address;
import dev.qeats.restaurant_management_service.repository.AddressRepository;
import dev.qeats.restaurant_management_service.model.Branch;
import dev.qeats.restaurant_management_service.model.Restaurant;
import dev.qeats.restaurant_management_service.repository.BranchRepository;
import dev.qeats.restaurant_management_service.repository.RestaurantRepository;
import dev.qeats.restaurant_management_service.responseVO.BranchVO;
import dev.qeats.restaurant_management_service.responseVO.RestaurantVO;
import dev.qeats.restaurant_management_service.service.BranchService;
import dev.qeats.restaurant_management_service.service.InterfaceB;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service("branchService")
public class BranchServiceImpl implements BranchService, InterfaceB {

    private final RestaurantRepository restaurantRepository;
    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;


    public BranchServiceImpl(RestaurantRepository restaurantRepository, BranchRepository branchRepository, ModelMapper modelMapper,
                             AddressRepository addressRepository) {
        this.restaurantRepository = restaurantRepository;
        this.branchRepository = branchRepository;
        this.modelMapper = modelMapper;
        this.addressRepository = addressRepository;
    }


//    public void myDefaultMethod() {
//        BranchService.myDefaultMethod();
//    }

    @Override
    public RestaurantVO addBranch(Long restaurantId, BranchVO branchVO) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("No Restaurant found"));

        Branch branch = modelMapper.map(branchVO, Branch.class);

        branch.setRestaurant(restaurant);
        // Save the Address explicitly if it's transient
        if (branch.getAddress() != null) {
            Address savedAddress = addressRepository.save(branch.getAddress());
            branch.setAddress(savedAddress);
        }

        restaurant.getBranches().add(branch);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
//        Branch savedBranch = branchRepository.save(branch);
        return savedRestaurant.toRestaurantVO();
    }


    @Override
    public Branch getBranchById(Long branch) {
        return null;
    }
}
