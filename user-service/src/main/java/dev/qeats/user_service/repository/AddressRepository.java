package dev.qeats.user_service.repository;

import dev.qeats.user_service.model.Address;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AddressRepository extends R2dbcRepository<Address, Long> {
//    Address findByAddressId(long addressId);
//
    @Query("SELECT * FROM addresses WHERE user_id = ?")
    Flux<Address> findByUserId(String userId);

    @Query("DELETE FROM addresses WHERE user_id = ?")
    Mono<Object> deleteByUserId(String userId);


    // setup a new reactive project
    // using mysql
    // create a very simple cart system project. very simple
    // only 2 tables
    // create a crud apis.
    // takes 1 hour


    // pending things
    // profiel api - done
    // cart api - 10mins
    // order api - 20mins
    // payment gateway integration - 1hr

    // integration of profile api with frontend - 10mins
    // cart api integration - 10 mins
    // confirm address integration - 10 mins
    // restaurant management integration - 10 mins

    // total how many mins?
    // 1.5 hrs

}
