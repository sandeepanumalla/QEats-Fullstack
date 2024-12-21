package dev.qeats.restaurant_management_service.repository;

import dev.qeats.restaurant_management_service.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

}
