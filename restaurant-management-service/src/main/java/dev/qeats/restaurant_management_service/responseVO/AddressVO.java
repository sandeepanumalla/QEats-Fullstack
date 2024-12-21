package dev.qeats.restaurant_management_service.responseVO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressVO {
    private long id;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String street;
    private double latitude;
    private double longitude;
}
