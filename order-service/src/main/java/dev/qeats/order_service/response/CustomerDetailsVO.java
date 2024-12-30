package dev.qeats.order_service.response;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerDetailsVO {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}