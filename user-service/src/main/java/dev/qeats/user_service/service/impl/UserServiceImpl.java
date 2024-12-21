package dev.qeats.user_service.service.impl;

import dev.qeats.user_service.model.Address;
import dev.qeats.user_service.model.Cart;
import dev.qeats.user_service.model.KeycloakUserInfo;
import dev.qeats.user_service.model.User;
import dev.qeats.user_service.repository.AddressRepository;
import dev.qeats.user_service.repository.UserRepository;
import dev.qeats.user_service.request.UserRequestVo;
import dev.qeats.user_service.response.AddressVO;
import dev.qeats.user_service.response.UserProfileVO;
import dev.qeats.user_service.service.UserService;
import io.micrometer.observation.ObservationFilter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    @Autowired
    private DatabaseClient databaseClient;
    private final UserRepository userRepository;
//    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    private final WebClient webClient;

    public UserServiceImpl(UserRepository userRepository, WebClient webClient,
                           AddressRepository addressRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.webClient = webClient;
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void createUser(UserRequestVo userRequestVo) {
        // convert userRequestVO into User entity and save it
        User user = new User();
        user.setFirstName(userRequestVo.getName());
        user.setEmail(userRequestVo.getEmail());
        Cart cart = new Cart();
//        user.setCart(cart);
//        cart.setUser(user);
        userRepository.save(user);
    }

    @Override
    public void getUser() {
        // get user by id
        // return user

    }

    @Override
    public void updateUser() {

    }

    @Override
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void getAllUsers() {

    }

    @Override
    public Mono<AddressVO> addAddressToUser(String userId, AddressVO addressVO, String accessToken) {
        // add address
        Address address = modelMapper.map(addressVO, Address.class);
        Mono<UserProfileVO> userProfileVOMono= getUserProfile(userId, accessToken);
        return userProfileVOMono.switchIfEmpty(Mono.error(new IllegalStateException("cannot fetch user")))
                .flatMap(userProfileVO -> {
                    // Set the userId in the address
                    address.setUserId(userProfileVO.getUserId());

                    // Save the address and map it to AddressVO
                    return addressRepository.save(address)
                            .doOnNext(savedAddress -> System.out.println("Address saved: " + savedAddress))
                            .switchIfEmpty(Mono.error(new IllegalStateException("Address could not be saved")))
                            .map(savedAddress -> modelMapper.map(savedAddress, AddressVO.class));
                });
    }

    private Mono<User> fetchAndCreateUserFromKeycloak(String accessToken, String userInfoUrl) {
        return webClient.get()
                .uri(userInfoUrl)
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KeycloakUserInfo.class)
                .flatMap(keycloakUserInfo -> {
                    if (keycloakUserInfo == null) {
                        return Mono.error(new Exception("User not found in Keycloak"));
                    }

                    // Create and save the new user
                    User newUser = new User();
                    newUser.setId(keycloakUserInfo.getSub()); // Generate a unique ID
                    newUser.setFirstName(keycloakUserInfo.getGiven_name());
                    newUser.setLastName(keycloakUserInfo.getFamily_name());
                    newUser.setEmail(keycloakUserInfo.getEmail());
                    newUser.setPhoneNumber(null);

                    // Use the upsert repository method
                    return userRepository.upsertUser(
                                    newUser.getId(),
                                    newUser.getFirstName(),
                                    newUser.getLastName(),
                                    newUser.getEmail()
                            )
                            .doOnSuccess(saved -> log.info("User upserted: {}", newUser))
                            .doOnError(error -> log.error("Error upserting user", error))
                            .thenReturn(newUser);
                })
                .onErrorResume(error -> {
                    log.error("Failed to fetch user from Keycloak or upsert user", error);
                    return Mono.error(new Exception("Failed to fetch user from Keycloak or create user", error));
                });
    }


    @Override
    public Mono<UserProfileVO> getUserProfile(String userId, String accessToken) {
        String userInfoUrl = "http://localhost:8765/info";

        // Fetch user from the database
        Mono<User> userMono = userRepository.findUserWithoutRoleById(userId);

        // Fetch user info from Keycloak or create a new user
        Mono<User> fetchOrCreateUserMono = userMono.switchIfEmpty(fetchAndCreateUserFromKeycloak(accessToken, userInfoUrl));

        // Fetch addresses as a list
        Mono<List<AddressVO>> addressesMono = addressRepository.findAll()
                .map(address -> modelMapper.map(address, AddressVO.class)) // Map Address to AddressVO
                .collectList(); // Collect all addresses into a List<AddressVO>

        // Build the UserProfileVO reactively
        return fetchOrCreateUserMono.zipWith(addressesMono) // Combine user and addresses
                .map(tuple -> {
                    User user = tuple.getT1();
                    List<AddressVO> addressVOs = tuple.getT2();

                    System.out.println(addressVOs.size());
                    UserProfileVO userProfile = new UserProfileVO();
                    userProfile.setUserId(user.getId());
                    userProfile.setFirstName(user.getFirstName());
                    userProfile.setLastName(user.getLastName());
                    userProfile.setEmail(user.getEmail());
                    userProfile.setPhoneNumber(null);
                    userProfile.setAddresses(addressVOs); // Set the list of addresses
                    return userProfile;
                });
    }


    @Override
    public UserProfileVO updateUserProfile(String userId, UserProfileVO userProfileVO) throws Exception {
        Mono<User> monoUser = userRepository.findById(userId).switchIfEmpty(Mono.error(new Exception("User not found")));

//        User user = monoUser.get();
//        user.setName(userProfileVO.getFirstName());
//        user.setEmail(userProfileVO.getEmail());
//        user.setPhoneNumber(userProfileVO.getPhoneNumber());

        // Save updated user back to the database
//        userRepository.save(user);
//
//        // Return updated profile
//        userProfileVO.setUserId(user.getId());
//        return userProfileVO;
        return null;
    }
    @Override
    public Mono<List<Address>> getUserAddress(String userId) {
        // Fetch the list of addresses from the repository
        log.info("yay i reecoved" + userId);
        return addressRepository.findByUserId(userId).collectList();
    }
    public Mono<Void> updateUserAddresses(UserProfileVO userProfileVO) {
        return addressRepository.deleteByUserId(userProfileVO.getUserId())
                .doOnSuccess(unused -> System.out.println("Deleted existing addresses for user: " + userProfileVO.getUserId()))
                .thenMany(
                        Flux.fromIterable(userProfileVO.getAddresses())
                                .map(addressVO -> new Address(
                                        0, // Let the database auto-generate the ID
                                        userProfileVO.getUserId(),
                                        addressVO.getStreet(),
                                        addressVO.getCity(),
                                        addressVO.getState(),
                                        addressVO.getZipCode(),
                                        addressVO.getCountry()
                                ))
                                .doOnNext(address -> System.out.println("Saving address: " + address))
                                .flatMap(addressRepository::save)
                )
                .doOnComplete(() -> System.out.println("All addresses saved successfully"))
                .then();
    }

    public Mono<Void> updateUserProfile(UserProfileVO userProfileVO) {

        // Update user details
        return userRepository.findById(userProfileVO.getUserId())
                .flatMap(user -> {
                    user.setFirstName(userProfileVO.getFirstName());
                    user.setLastName(userProfileVO.getLastName());
                    user.setEmail(userProfileVO.getEmail());
                    user.setPhoneNumber(userProfileVO.getPhoneNumber());
                    return userRepository.save(user);
                })
                .thenMany(
                        // Update or save addresses
                        Flux.fromIterable(userProfileVO.getAddresses())
                                .flatMap(addressVO -> {
                                    if (addressVO.getId() != 0) {
                                        // Update existing address
                                        return addressRepository.findById(addressVO.getId())
                                                .flatMap(existingAddress -> {
                                                    existingAddress.setCity(addressVO.getCity());
                                                    existingAddress.setState(addressVO.getState());
                                                    existingAddress.setCountry(addressVO.getCountry());
                                                    existingAddress.setZipCode(addressVO.getZipCode());
                                                    existingAddress.setStreet(addressVO.getStreet());
                                                    return addressRepository.save(existingAddress);
                                                });
                                    } else {
                                        // Save new address
                                        Address newAddress = new Address(
                                                0, // New ID will be auto-generated
                                                userProfileVO.getUserId(),
                                                addressVO.getStreet(),
                                                addressVO.getCity(),
                                                addressVO.getState(),
                                                addressVO.getZipCode(),
                                                addressVO.getCountry()
                                        );
                                        return addressRepository.save(newAddress);
                                    }
                                })
                )
                .then(); // Signal completion
    }


    public ObservationFilter getUserRestaurants(String userId) {
        String url = "http://localhost:8765/api/restaurant";
        return null;
    }
}
