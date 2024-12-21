package dev.qeats.order_service.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class DeliveryAddress {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}
