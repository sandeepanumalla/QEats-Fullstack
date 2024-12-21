package dev.qeats.restaurant_management_service.repository;

import dev.qeats.restaurant_management_service.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}