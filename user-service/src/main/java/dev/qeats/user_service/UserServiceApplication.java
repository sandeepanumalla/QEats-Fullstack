package dev.qeats.user_service;

//import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
//import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
//import io.swagger.v3.oas.annotations.security.OAuthFlow;
//import io.swagger.v3.oas.annotations.security.OAuthFlows;
//import io.swagger.v3.oas.annotations.security.OAuthScope;
//import io.swagger.v3.oas.annotations.security.SecurityScheme;
import dev.qeats.user_service.model.Address;
import dev.qeats.user_service.repository.AddressRepository;
import dev.qeats.user_service.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@SpringBootApplication
@EnableR2dbcRepositories
public class UserServiceApplication {

	@Autowired
	public AddressRepository addressRepository;
	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@PostConstruct
	public void post() {
//		Flux.just("abd", "dfdf").subscribe(each -> System.out.println(each));
//		addressRepository.findAll().subscribe(each -> System.out.println(each));
		addressRepository.findByUserId("77055c49-2dfe-4aec-8f15-406a09289e34").subscribe((address) -> System.out.println(address.getState()));
		userRepository.findUserWithoutRoleById("77055c49-2dfe-4aec-8f15-406a09289e34").subscribe((user) -> System.out.println(user.getEmail()));
//		addressRepository.deleteByUserId("77055c49-2dfe-4aec-8f15-406a09289e34").doOnNext(a -> System.out.println("done " + a)).subscribe();
	}


}
