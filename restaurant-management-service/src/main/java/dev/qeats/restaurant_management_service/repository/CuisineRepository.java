package dev.qeats.restaurant_management_service.repository;

import dev.qeats.restaurant_management_service.model.Cuisine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuisineRepository extends JpaRepository<Cuisine, Long>, JpaSpecificationExecutor<Cuisine> {

    @Query("SELECT c FROM Cuisine c WHERE c.id IN (SELECT MIN(c2.id) FROM Cuisine c2 GROUP BY c2.name)")
    List<Cuisine> findDistinctCuisineNames();



}
