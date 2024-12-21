package dev.qeats.order_service.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryAddressVO {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}
