package dev.qeats.restaurant_management_service.service;

import dev.qeats.restaurant_management_service.model.Branch;
import dev.qeats.restaurant_management_service.responseVO.BranchVO;
import dev.qeats.restaurant_management_service.responseVO.RestaurantVO;
import org.springframework.stereotype.Service;

@Service
public interface BranchService {
     static void myDefaultMethod() {
        System.out.println("This is my default method from branch service");
    }
    public RestaurantVO addBranch(Long restaurantId, BranchVO branchVO);
    Branch getBranchById(Long branch);
}
