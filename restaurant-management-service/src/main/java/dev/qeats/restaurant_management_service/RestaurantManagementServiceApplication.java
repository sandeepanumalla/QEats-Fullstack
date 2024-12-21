package dev.qeats.restaurant_management_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;

@SpringBootApplication
public class RestaurantManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantManagementServiceApplication.class, args);
	}

}
