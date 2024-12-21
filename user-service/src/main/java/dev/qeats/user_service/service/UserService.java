package dev.qeats.user_service.service;

import dev.qeats.user_service.model.Address;
import dev.qeats.user_service.request.UserRequestVo;
import dev.qeats.user_service.response.AddressVO;
import dev.qeats.user_service.response.UserProfileVO;
import reactor.core.publisher.Mono;

import java.util.List;


public interface UserService {

    public Mono<List<Address>> getUserAddress(String userId);
    public void createUser(UserRequestVo userRequestVo);
    public void getUser();
    public void updateUser();
    public void deleteUser(String userId);
    public void getAllUsers();
    public Mono<AddressVO> addAddressToUser(String userId, AddressVO addressVO, String accessToken);
    public Mono<UserProfileVO> getUserProfile(String userId, String accessToken);
    UserProfileVO updateUserProfile(String userId, UserProfileVO userProfileVO) throws Exception;
}
