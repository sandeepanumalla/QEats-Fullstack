package dev.qeats.user_service;

import dev.qeats.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

//@SpringBootTest
@DataR2dbcTest
@ExtendWith(SpringExtension.class)
@Testcontainers
@ActiveProfiles(value = "test")
class UserServiceApplicationTests {

	@Container
	private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.28")
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");


	@Autowired
	private UserRepository userRepository;


	@Test
	void contextLoads() {
	}




}
