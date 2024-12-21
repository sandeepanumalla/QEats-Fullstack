package dev.qeats.user_service.repository;

import dev.qeats.user_service.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {
    @Query("SELECT * FROM users WHERE id = :id")
    Mono<User> findUserWithoutRoleById(String id);

    Mono<User> findByEmail(String email);

    @Query("INSERT INTO users (id, first_name, last_name, email) VALUES (:id, :firstName, :lastName, :email) " +
            "ON DUPLICATE KEY UPDATE first_name = :firstName, last_name = :lastName, email = :email")
    Mono<Void> upsertUser(String id, String firstName, String lastName, String email);


}
