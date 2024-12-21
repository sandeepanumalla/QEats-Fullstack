package dev.qeats.user_service.respository;

import dev.qeats.user_service.UserServiceApplication;
import dev.qeats.user_service.model.User;
import dev.qeats.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = UserServiceApplication.class)
//@ActiveProfiles("test")
//@DataR2dbcTest
@ExtendWith(SpringExtension.class)
//@Testcontainers
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a sample user for the test
//        testUser = new User();
//        testUser.setFirstName("John");
//        testUser.setLastName("Doe");
//        testUser.setId("123josd");
//        testUser.setEmail("john.doe@example.com");
//
//        userRepository.save(testUser);  // Save the user to the in-memory database
    }

    @Test
    void testFindByUserId() {
        // Act
        Mono<User> foundUser = userRepository.findById("sdf");

        // Assert
//        assertThat(foundUser).isNotNull();
//        assertThat(foundUser.get().getId()).isEqualTo(1L);
//        assertThat(foundUser.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void testSaveUser() {
        // Create and save a new user
        User newUser = new User();
//        newUser.setId("kn123kndf");
        newUser.setFirstName("Jane");
        newUser.setLastName("doe");
        newUser.setEmail("jane.doe@example.com");

        Mono<User> savedUser = userRepository.save(newUser);

        // Assert that the new user was saved correctly
//        assertThat(savedUser.getId()).isEqualTo(2L);
//        assertThat(savedUser.getName()).isEqualTo("Jane Doe");
//        assertThat(savedUser.getEmail()).isEqualTo("jane.doe@example.com");
    }

    @Test
    void testFindUserNotFound() {
        // Act
//        Optional<User> notFoundUser = userRepository.findById(99L);

        // Assert that no user is found for an invalid userId
//        assertThat(notFoundUser).isNull();
    }

    @Test
    void testDeleteUser() {
        // Act
//        userRepository.deleteById(testUser.getId());
//
//        // Try to find the deleted user
//        Optional<User> deletedUser = userRepository.findById(testUser.getId());

        // Assert
//        assertThat(deletedUser).isEmpty();  // Make sure the user is deleted
    }

}
